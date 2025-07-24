package com.example.routime.Presentation.UiControllers

import ContextViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routime.Data.Models.Deed
import com.example.routime.ExtrasNames
import com.example.routime.Presentation.UiControllers.UiAdapters.DeedItemsListRecyclerViewAdapter
import com.example.routime.Presentation.ViewModels.TodayDeedViewModel
import com.example.routime.databinding.FragmentTodayDeedBinding


import kotlinx.coroutines.launch

class TodayDeedFragment : Fragment() {
    private lateinit var xmlViews : FragmentTodayDeedBinding
    private var latestDeedList : List<Deed>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return FragmentTodayDeedBinding.inflate(layoutInflater,container,false).run {
            xmlViews = this
            root
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onCreate(savedInstanceState)


        val todayDeedViewModel : TodayDeedViewModel by viewModels{
            ContextViewModelFactory(requireActivity().applicationContext)
        }

        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                todayDeedViewModel.todayDeeds
                    .collect{
                        if (it==latestDeedList) return@collect
                        println("Today Deed List Count : ${it.size}")
                        latestDeedList = it

                        updateOverViewLayout(it)
                        when(it.size){
                            0-> hideRecyclerView()
                            else->{
                                showRecyclerView()
                                with(xmlViews.TodayDeedIListRecyclerView){
                                    layoutManager = LinearLayoutManager(requireContext())
                                    itemAnimator = DefaultItemAnimator()
                                    adapter = DeedItemsListRecyclerViewAdapter(it,::launchShowDeedActivity)
                                }
                            }
                        }

                    }
            }
        }

        xmlViews.MainAddDeedButton.setOnClickListener {
            startActivity(Intent(requireContext() , AddNewDeedActivity::class.java))
        }

    }

    private fun updateOverViewLayout(it: List<Deed>) {
        xmlViews.TodayDeedCountTextView.text = it.size.toString()
        xmlViews.TodayDeedTimeSpentTextView.text = it.sumOf { it.timeSpent }.toString()+" min"

        val totalProgressValue = if(it.isEmpty()) 0f else (it.sumOf { it.progress } / it.size).toFloat()
        xmlViews.TodayDeedDoneProgress.value = totalProgressValue
        xmlViews.TodayDeedProgressTextView.text = "Progress: ${totalProgressValue.toInt()}%"
        xmlViews.TodayDeedDonePercentTextView.text = "${it.count { it.done }}/${it.size} Of Deeds Completed"
    }

    private fun showRecyclerView(){
        xmlViews.TodayDeedIListRecyclerView.visibility = View.VISIBLE
        xmlViews.NoDeedTodayInfoImageView.visibility = View.INVISIBLE
    }

    private fun hideRecyclerView(){
        xmlViews.TodayDeedIListRecyclerView.visibility = View.INVISIBLE
        xmlViews.NoDeedTodayInfoImageView.visibility = View.VISIBLE
    }

    private fun launchShowDeedActivity(deed : Deed){
        Intent(requireContext(),ShowDeedActivity::class.java).apply {
            putExtra(ExtrasNames.PARCELABLE_DEED.name,deed)
        }.also(::startActivity)
    }
}