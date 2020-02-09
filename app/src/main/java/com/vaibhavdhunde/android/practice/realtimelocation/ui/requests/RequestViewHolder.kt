package com.vaibhavdhunde.android.practice.realtimelocation.ui.requests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.databinding.ItemFriendRequestBinding
import com.vaibhavdhunde.android.practice.realtimelocation.model.User

class RequestViewHolder(
    private val itemBinding: ItemFriendRequestBinding,
    private val viewModel: RequestsViewModel
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(user: User) {
        itemBinding.user = user
        itemBinding.listener = this@RequestViewHolder.listener
        itemBinding.executePendingBindings()
    }

    private val listener = object : RequestsListUserActionsListener {
        override fun onAcceptFriendRequest(user: User) {
            viewModel.onAcceptFriendRequest(user)
        }

        override fun onDeclineFriendRequest(user: User) {
            viewModel.onDeclineFriendRequest(user)
        }
    }

    companion object {
        operator fun invoke(parent: ViewGroup, viewModel: RequestsViewModel): RequestViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_friend_request, parent, false)
            val binding = ItemFriendRequestBinding.bind(itemView)
            return RequestViewHolder(binding, viewModel)
        }
    }

}