package ru.students.dvfu.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.students.dvfu.databinding.FragmentUsersBinding
import ru.students.dvfu.mvp.model.FirebaseUsersRepo
import ru.students.dvfu.mvp.presenter.UsersPresenter
import ru.students.dvfu.mvp.view.UsersView
import ru.students.dvfu.ui.BackButtonListener
import ru.students.dvfu.ui.adapter.UsersRVAdapter

/**
 * A fragment representing a list of Items.
 */
class UsersFragment : MvpAppCompatFragment(), UsersView, BackButtonListener {
    companion object {
        fun newInstance() = UsersFragment()
    }

    val presenter: UsersPresenter by moxyPresenter {
        UsersPresenter(
            this,
            AndroidSchedulers.mainThread(),
            FirebaseUsersRepo(),
            //App.instance.router, AndroidScreens()
        )
    }

    var adapter: UsersRVAdapter? = null

    private var vb: FragmentUsersBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentUsersBinding.inflate(inflater, container, false).also {
            vb = it
        }.root

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }

    override fun init() {
        vb?.rvUsers?.layoutManager = LinearLayoutManager(context)
        adapter = UsersRVAdapter(presenter.usersListPresenter
            //, GlideImageLoader()
        )
        vb?.rvUsers?.adapter = adapter

    }

    override fun updateList() {
        adapter?.notifyDataSetChanged()
    }

    override fun setProgress(value: Boolean) {
        if (value) vb?.usersProgressBar?.visibility = View.VISIBLE
        else vb?.usersProgressBar?.visibility = View.INVISIBLE
    }

    override fun backPressed() = presenter.backPressed()

}