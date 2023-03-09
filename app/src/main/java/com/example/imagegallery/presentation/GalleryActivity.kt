package com.example.imagegallery.presentation

import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagegallery.R
import com.example.imagegallery.data.model.Status
import com.example.imagegallery.data.service.DeviceAuthenticationService
import com.example.imagegallery.databinding.ActivityGalleryBinding
import com.example.imagegallery.extension.hideKeyboard
import com.example.imagegallery.imageloading.ImageLoader
import com.example.imagegallery.util.GridSpacingItemDecoration
import com.example.imagegallery.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {

    @Inject
    lateinit var adapter: GalleryImageAdapter

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var deviceAuthenticationService: DeviceAuthenticationService

    private lateinit var binding: ActivityGalleryBinding
    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViews()
        observeViewState()
        initBioMetrics()
    }

    private fun initBioMetrics() {
        deviceAuthenticationService.authenticateWithBiometric(
            this,
            completion = { result, message ->
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setMessage(message)
                    .setPositiveButton(
                        R.string.ok
                    ) { _, _ ->
                        if (result) {
                            binding.apply {
                                inputLayout.placeholderText =
                                    getString(R.string.search_hint_to_enter)
                                edtSearch.requestFocus()
                                edtSearch.inputType = InputType.TYPE_CLASS_TEXT
                            }
                        } else {
                            binding.apply {
                                inputLayout.placeholderText =
                                    getString(R.string.search_hint_to_activate)
                                edtSearch.requestFocus()
                                edtSearch.inputType = InputType.TYPE_NULL
                            }
                        }
                    }
                    .show()
            })
    }

    private fun initViews() {
        binding.apply {
            recyclerviewGallery.setHasFixedSize(true)
            val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 3)
            recyclerviewGallery.layoutManager = layoutManager

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_item_spacing)
            recyclerviewGallery.addItemDecoration(GridSpacingItemDecoration(spacingInPixels))
            setupScrollListener()
            recyclerviewGallery.adapter = adapter

            edtSearch.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    updateListFromInput()
                    true
                } else {
                    false
                }
            }

            edtSearch.setOnClickListener {
                if (edtSearch.inputType == InputType.TYPE_NULL) {
                    initBioMetrics()
                }
            }

            inputLayout.setEndIconOnClickListener {
                edtSearch.setText("")
            }

            showErrorInformation(adapter.itemCount)
        }
    }

    private fun updateListFromInput() {
        binding.edtSearch.text?.trim()?.let {
            if (it.isNotEmpty()) {
                hideKeyboard()
                binding.progressBar.visibility = View.VISIBLE
                binding.txtEmptyList.visibility = View.GONE
                // clear adapter before searching fo new photos
                adapter.clearList()
                viewModel.searchPhotos(binding.edtSearch.text.toString().trim(), 1)
            }
        }
    }

    private fun observeViewState() {
        viewModel.photoList.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { photos ->
                        adapter.addPhotos(photos.photo)
                        showSuccessState()
                    }
                }
                Status.LOADING -> showLoadingState()
                Status.ERROR -> showErrorState(it.message)
            }
        }
    }

    private fun showSuccessState() {
        binding.progressBar.visibility = View.GONE
        showErrorInformation(adapter.itemCount)
    }

    private fun showErrorState(message: String?) {
        binding.progressBar.visibility = View.GONE
        showErrorInformation(adapter.itemCount, message)
    }

    private fun showLoadingState() {
        //No-op
    }

    private fun setupScrollListener() {
        val layoutManager = binding.recyclerviewGallery.layoutManager as GridLayoutManager
        binding.recyclerviewGallery.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // this calls view model method multiple times
                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }
        })
    }

    private fun showErrorInformation(
        count: Int,
        message: String? = getString(R.string.default_no_results_msg)
    ) {
        message?.let {
            binding.txtEmptyList.text = message
        }

        if (count == 0) {
            binding.txtEmptyList.visibility = View.VISIBLE
        } else {
            binding.txtEmptyList.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageLoader.clearCache()
    }
}
