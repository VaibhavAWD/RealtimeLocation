package com.vaibhavdhunde.android.practice.realtimelocation.ui.requests

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.vaibhavdhunde.android.practice.realtimelocation.R
import com.vaibhavdhunde.android.practice.realtimelocation.databinding.ActivityRequestsBinding
import com.vaibhavdhunde.android.practice.realtimelocation.util.*
import kotlinx.android.synthetic.main.activity_requests.*
import org.jetbrains.anko.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

class RequestsActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()

    private lateinit var viewModel: RequestsViewModel

    private lateinit var binding: ActivityRequestsBinding

    private lateinit var requestsAdapter: RequestListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataBinding()

        setupSwipeRefresh()

        setupEvents()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        NotificationUtils.clearAllNotifications(this)
    }

    private fun setupDataBinding() {
        viewModel = obtainViewModel(RequestsViewModel::class.java)

        requestsAdapter = RequestListAdapter(viewModel)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_requests)
        binding.apply {
            viewmodel = this@RequestsActivity.viewModel
            adapter = requestsAdapter
            lifecycleOwner = this@RequestsActivity
        }
    }

    private fun setupEvents() {
        binding.viewmodel?.showProgress?.observe(this, Observer { show ->
            if (show) {
                showSpotsDialog(R.string.progress_dialog_accepting_request)
            } else {
                hideSpotsDialog()
            }
        })

        binding.viewmodel?.showRefreshing?.observe(this, Observer { show ->
            refresh_requests.isRefreshing = show
        })

        binding.viewmodel?.showMessage?.observe(this, EventObserver { message ->
            toast(message)
        })

        binding.viewmodel?.requests?.observe(this, Observer { listRequests ->
            requestsAdapter.submitList(listRequests)
        })
    }

    private fun setupNavigation() {
        binding.viewmodel?.navigateBackToMainActivity?.observe(this, EventObserver {
            finish()
        })
    }

    private fun setupSwipeRefresh() {
        refresh_requests.setOnRefreshListener {
            binding.viewmodel?.reloadData()
        }
    }
}
