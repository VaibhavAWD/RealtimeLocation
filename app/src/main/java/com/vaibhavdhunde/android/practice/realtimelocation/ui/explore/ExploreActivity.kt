package com.vaibhavdhunde.android.practice.realtimelocation.ui.explore

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.databinding.ActivityExploreBinding
import com.vaibhavdhunde.android.practice.realtimelocation.model.User
import com.vaibhavdhunde.android.practice.realtimelocation.util.*
import kotlinx.android.synthetic.main.activity_explore.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ExploreActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()

    private lateinit var viewModel: ExploreViewModel

    private val viewModelFactory: MyViewModelFactory by instance()

    private lateinit var binding: ActivityExploreBinding

    private lateinit var peopleAdapter: PeopleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataBinding()
        setupListPeople()
        setupSwipeRefresh()
        setupEvents()
    }

    private fun setupDataBinding() {
        viewModel = obtainViewModel(ExploreViewModel::class.java, viewModelFactory)

        peopleAdapter = PeopleListAdapter(viewModel)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_explore)
        binding.apply {
            viewmodel = this@ExploreActivity.viewModel
            adapter = peopleAdapter
            lifecycleOwner = this@ExploreActivity
        }
    }

    private fun setupEvents() {
        binding.viewmodel?.showFriendRequestDialog?.observe(this, EventObserver { user ->
            showFriendRequestDialog(user)
        })

        binding.viewmodel?.showProgress?.observe(this, Observer { show ->
            if (show) {
                showSpotsDialog(R.string.progress_dialog_sending_friend_request)
            } else {
                hideSpotsDialog()
            }
        })

        binding.viewmodel?.showRefreshing?.observe(this, Observer { show ->
            refresh_people.isRefreshing = show
        })

        binding.viewmodel?.showMessage?.observe(this, EventObserver { message ->
            toast(message)
        })

        binding.viewmodel?.people?.observe(this, Observer { listPeople ->
            peopleAdapter.submitList(listPeople)
        })
    }

    private fun showFriendRequestDialog(user: User) {
        val title = getString(R.string.dialog_title_friend_request)
        val message = "Do you want to send friend request to ${user.name} (${user.email})"
        alert(message, title) {
            setTheme(R.style.DialogTheme)
            noButton { it.dismiss() }
            positiveButton(getString(R.string.option_send)) {
                binding.viewmodel?.checkRequestAlreadySent(user)
            }
        }.show()
    }

    private fun setupListPeople() {
        list_people.setHasFixedSize(true)
        list_people.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
    }

    private fun setupSwipeRefresh() {
        refresh_people.setOnRefreshListener {
            binding.viewmodel?.reloadData()
        }
    }
}
