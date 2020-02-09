package com.vaibhavdhunde.android.practice.realtimelocation.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.databinding.ItemFriendBinding
import com.vaibhavdhunde.android.practice.realtimelocation.model.User

class FriendViewHolder(
    private val itemFriendBinding: ItemFriendBinding,
    private val viewModel: MainViewModel
) : RecyclerView.ViewHolder(itemFriendBinding.root) {

    fun bind(user: User) {
        itemFriendBinding.user = user
        itemFriendBinding.listener = this@FriendViewHolder.listener
        itemFriendBinding.executePendingBindings()
    }

    private val listener = object : FriendsListActionListener {
        override fun onClickFriend(user: User) {
            viewModel.onClickFriend(user)
        }
    }

    companion object {
        operator fun invoke(parent: ViewGroup, viewModel: MainViewModel): FriendViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_friend, parent, false)
            val binding = ItemFriendBinding.bind(itemView)
            return FriendViewHolder(binding, viewModel)
        }
    }

}