package ru.students.dvfu.mvp.view.list

interface UserItemView: IItemView {
    fun setName(name: String)
    fun setEmail(email: String)
    fun setRole(role: String)
    fun loadAvatar(url: String)
}