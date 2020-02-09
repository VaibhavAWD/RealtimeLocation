package com.vaibhavdhunde.android.practice.realtimelocation.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.databinding.ItemUserBinding
import com.vaibhavdhunde.android.practice.realtimelocation.firebase.FireAuth
import com.vaibhavdhunde.android.practice.realtimelocation.model.User

class UserViewHolder(
    private val itemUserBinding: ItemUserBinding,
    private val viewModel: ExploreViewModel
) : RecyclerView.ViewHolder(itemUserBinding.root) {

    fun bind(user: User) {
        itemUserBinding.user = user
        itemUserBinding.listener = this@UserViewHolder.listener
        itemUserBinding.executePendingBindings()
    }

    private val listener = object : ListUsersActionListener {
        override fun onClickUser(user: User) {
            if (user.uid == FireAuth.getCurrentUserId()) {
                return
            }
            viewModel.onClickUser(user)
        }
    }

    companion object {
        operator fun invoke(parent: ViewGroup, viewModel: ExploreViewModel): UserViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)
            val binding = ItemUserBinding.bind(itemView)
            return UserViewHolder(binding, viewModel)
        }
    }

}