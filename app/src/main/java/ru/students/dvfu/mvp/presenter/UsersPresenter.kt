package ru.students.dvfu.mvp.presenter

import io.reactivex.rxjava3.core.Scheduler
import moxy.MvpPresenter
import ru.students.dvfu.mvp.presenter.list.IUserListPresenter
import ru.students.dvfu.mvp.view.list.UserItemView
import ru.students.dvfu.mvp.model.IFirebaseUsersRepo
import ru.students.dvfu.mvp.model.entity.FirebaseUser
import ru.students.dvfu.mvp.view.UsersView

class UsersPresenter(
    private val usersView: UsersView,
    private val uiScheduler: Scheduler,
    private val firebaseUsersRepo: IFirebaseUsersRepo
    ) : MvpPresenter<UsersView>() {

    class UsersListPresenter : IUserListPresenter {
        val users = mutableListOf<FirebaseUser>()
        override var itemClickListener: ((UserItemView) -> Unit)? = null

        override fun getCount() = users.size

        override fun bindView(view: UserItemView) {
            val user = users[view.pos]
            view.setName(user.name)
            view.setEmail(user.email)
            view.loadAvatar(user.avatar)
        }
    }

    val usersListPresenter = UsersListPresenter()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()


        loadData()

        usersListPresenter.itemClickListener = { itemView ->
            val user = usersListPresenter.users[itemView.pos]
            //router.navigateTo(screens.user(user))
        }
    }


    private fun loadData() {
        viewState.setProgress(true)
        firebaseUsersRepo.getUsers()
            .observeOn(uiScheduler)
            .subscribe({ users ->
                usersListPresenter.users.clear()
                usersListPresenter.users.addAll(users)
                viewState.updateList()
                viewState.setProgress(false)
            }, {
                println("Error: ${it.message}")
                viewState.setProgress(false)
            })
    }

    fun backPressed(): Boolean {
        //router.exit()
        return true
    }
}