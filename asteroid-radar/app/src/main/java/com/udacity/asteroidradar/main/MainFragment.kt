package com.udacity.asteroidradar.main

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.dto.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    companion object {
        private const val PREFERENCES_URL = "picture_of_day_url"
        private const val PREFERENCES_TITLE = "picture_of_day_title"
        private const val PREFERENCES_MEDIA_TYPE = "picture_of_day_media_type"
    }

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

        val adapter =  AsteroidListAdapter(AsteroidListAdapter.AsteroidClickListener {
            viewModel.onNavigateToAsteroidDetail(it)
        })
        binding.asteroidRecycler.adapter = adapter

        viewModel.asteroids.observe(viewLifecycleOwner, Observer { asteroids ->
            asteroids?.let {
                adapter.submitList(asteroids)
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

        loadPictureOfDay()
        setHasOptionsMenu(true)

        return binding.root
    }

    /**
     * In a case that the worker did not run yet, we want to show a picture
     */
    private fun loadPictureOfDay() {
        val existingPictureOfDay = getPictureOfDayFromPreferences()
        if (existingPictureOfDay != null) {
            setPictureOfDay(existingPictureOfDay)
        } else {
            viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer { pictureOfDay ->
                pictureOfDay?.let {
                    setPictureOfDay(pictureOfDay)
                    savePictureOfDayInPreferences(pictureOfDay)
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

    private fun getPictureOfDayFromPreferences(): PictureOfDay? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val pictureUrl = sharedPref?.getString(PREFERENCES_URL, null)
        val pictureTitle = sharedPref?.getString(PREFERENCES_TITLE, null)
        val pictureMediaType = sharedPref?.getString(PREFERENCES_MEDIA_TYPE, null)

        return if(pictureUrl != null && pictureTitle != null && pictureMediaType != null) {
            PictureOfDay(pictureMediaType, pictureTitle, pictureUrl)
        } else {
            null
        }
    }

    private fun savePictureOfDayInPreferences(pictureOfDay: PictureOfDay) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(PREFERENCES_URL, pictureOfDay.url)
            putString(PREFERENCES_TITLE, pictureOfDay.title)
            putString(PREFERENCES_MEDIA_TYPE, pictureOfDay.mediaType)
            apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
