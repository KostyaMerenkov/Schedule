package ru.dvfu.appliances.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.dvfu.appliances.databinding.FragmentUsersBinding
import ru.dvfu.appliances.model.userdata.User
import ru.dvfu.appliances.ui.activity.UsersViewModel
import ru.dvfu.appliances.ui.adapter.UsersRVAdapter

class UsersFragment : Fragment() {

    lateinit var model: UsersViewModel
    private lateinit var vb: FragmentUsersBinding
    private val adapter: UsersRVAdapter by lazy { UsersRVAdapter() }
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
        FragmentUsersBinding.inflate(inflater, container, false).also {
            vb = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        if (vb.rvUsers.adapter != null) {
            throw IllegalStateException("The ViewModel should be initialised first")
        }
        val viewModel: UsersViewModel by viewModel()
        model = viewModel
        model.subscribeUsers().observe(viewLifecycleOwner, { setDataToAdapter(it) })
        model.subscribeLoading().observe(viewLifecycleOwner, {
            vb.usersSwipeRefreshLayout.isRefreshing = it })
        model.getData()
    }

    private fun initViews() {
        vb.rvUsers.layoutManager = LinearLayoutManager(context)
        vb.rvUsers.adapter = adapter
        vb.usersSwipeRefreshLayout.setOnRefreshListener { model.getData() }
    }

    private fun setDataToAdapter(data: List<User>) {
        adapter.setData(data)
    }

}