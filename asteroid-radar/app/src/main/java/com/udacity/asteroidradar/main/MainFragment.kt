package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.dto.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.utility.SharedPreferencesUtility
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(requireActivity().application))
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.pullToRefresh.setOnRefreshListener {
            viewModel.reloadAsteroids {
                binding.pullToRefresh.isRefreshing = false
            }
        }

        val adapter = AsteroidListAdapter(AsteroidListAdapter.AsteroidClickListener {
            viewModel.onNavigateToAsteroidDetail(it)
        })
        binding.asteroidRecycler.adapter = adapter

        viewModel.showTodayAsteroids.observe(viewLifecycleOwner, Observer { today ->
            showSpinner()
            if (today == null) {
                adapter.submitList(viewModel.asteroids.value)
            } else {
                adapter.submitList(viewModel.asteroids.value?.filter { it.closeApproachDate == today })
            }
            hideSpinner()
        })
        viewModel.asteroids.observe(viewLifecycleOwner, Observer { asteroids ->
            if (asteroids.isEmpty()) {
                viewModel.loadData {
                    hideSpinner()
                }
            } else {
                adapter.submitList(asteroids)
                hideSpinner()
            }
        })
        viewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner, Observer { asteroid ->
            asteroid?.let {
                this.findNavController().navigate(
                    MainFragmentDirections
                        .actionShowDetail(asteroid)
                )
                viewModel.navigatingToAsteroidDetailDone()
            }
        })
        viewModel.networkError.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.networkError.visibility = View.VISIBLE
            }
        })

        showSpinner()
        loadPictureOfDay()
        setHasOptionsMenu(true)

        return binding.root
    }

    /**
     * In a case that the worker did not run yet, we want to show a picture
     */
    private fun loadPictureOfDay() {
        val existingPictureOfDay = SharedPreferencesUtility.getPictureOfDayFromPreferences(requireContext())
        if (existingPictureOfDay != null) {
            setPictureOfDay(existingPictureOfDay)
        } else {
            viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer { pictureOfDay ->
                pictureOfDay?.let {
                    setPictureOfDay(pictureOfDay)
                    SharedPreferencesUtility.savePictureOfDayInPreferences(requireContext(), pictureOfDay)
                }
            })
        }
    }

    private fun setPictureOfDay(pictureOfDay: PictureOfDay) {
        Picasso.with(context)
            .load(pictureOfDay.url)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .error(R.drawable.placeholder_picture_of_day)
            .into(binding.activityMainImageOfTheDay)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.show_all_menu -> {
                viewModel.showAllAsteroids()
            }
            R.id.show_today_menu -> {
                viewModel.setShowTodayAsteroids()
            }
        }
        return true
    }

    private fun showSpinner() {
        binding.statusLoadingWheel.visibility = View.VISIBLE
    }

    private fun hideSpinner() {
        binding.statusLoadingWheel.visibility = View.GONE
    }
}
