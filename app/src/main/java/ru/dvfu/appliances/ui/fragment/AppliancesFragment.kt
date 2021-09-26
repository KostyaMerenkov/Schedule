package ru.dvfu.appliances.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.dvfu.appliances.databinding.FragmentAppliancesBinding
import ru.dvfu.appliances.model.userdata.entities.Appliance
import ru.dvfu.appliances.ui.activity.AppliancesViewModel
import ru.dvfu.appliances.ui.adapter.AppliancesRVAdapter

class AppliancesFragment : Fragment() {

    lateinit var model: AppliancesViewModel
    private lateinit var vb: FragmentAppliancesBinding
    private val adapter: AppliancesRVAdapter by lazy { AppliancesRVAdapter() }
//    private val onListItemClickListener: UsersRVAdapter.OnListItemClickListener =
//        object : UsersRVAdapter.OnListItemClickListener {
//            override fun onItemClick(data: User) {
//                startActivity(
//                    DescriptionActivity.getIntent(
//                        this@MainActivity,
//                        data.text,
//                        convertMeaningsToSingleString(data.meanings),
//                        data.meanings[0].imageUrl
//                    )
//                )
//            }
//        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentAppliancesBinding.inflate(inflater, container, false).also {
            vb = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        if (vb.rvAppliances.adapter != null) {
            throw IllegalStateException("The ViewModel should be initialised first")
        }
        val viewModel: AppliancesViewModel by viewModel()
        model = viewModel
        model.subscribeAppliances().observe(viewLifecycleOwner, { setDataToAdapter(it) })
        model.subscribeLoading().observe(viewLifecycleOwner, {
            vb.appliancesSwipeRefreshLayout.isRefreshing = it })
        model.getData()
    }

    private fun initViews() {
        vb.rvAppliances.layoutManager = GridLayoutManager(context,2)
        vb.rvAppliances.adapter = adapter
        vb.appliancesSwipeRefreshLayout.setOnRefreshListener { model.getData() }
    }

    private fun setDataToAdapter(data: List<Appliance>) {
        adapter.setData(data)
    }

}