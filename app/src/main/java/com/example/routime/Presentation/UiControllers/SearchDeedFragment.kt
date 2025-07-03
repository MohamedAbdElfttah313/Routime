package com.example.routime.Presentation.UiControllers

import ContextViewModelFactory
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.routime.CategoryEnum
import com.example.routime.Data.Models.Deed
import com.example.routime.Emojis
import com.example.routime.ExtrasNames

import com.example.routime.Presentation.UiControllers.UiAdapters.DeedItemsListRecyclerViewAdapter
import com.example.routime.Presentation.UiAdapters.ImageViewSelectionRecyclerViewAdapter
import com.example.routime.Presentation.UiAdapters.SevenDaysRecyclerViewAdapter
import com.example.routime.Presentation.UiAdapters.StringSelectionRecyclerViewAdapter
import com.example.routime.Presentation.ViewModels.SearchDeedViewModel
import com.example.routime.R
import com.example.routime.databinding.FragmentSearchDeedBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.util.Calendar

//TODO(Calendar+Icons Selections+ clean Filter + ASC DESC sorting)

class SearchDeedFragment : Fragment() {
    private lateinit var xmlViews : FragmentSearchDeedBinding
    private val queryFlow = MutableStateFlow("")
    private val deedAttributes = listOf("Date","Time Spent")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentSearchDeedBinding.inflate(layoutInflater,container,false).run {
            xmlViews = this
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val searchViewModel : SearchDeedViewModel by viewModels {
            ContextViewModelFactory(requireActivity().applicationContext)
        }

        setUpFilterMenus(searchViewModel)
        setUpFilterSelectors()


        with(xmlViews.SevenDaysRecyclerView){
            val calendar = Calendar.getInstance()
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            itemAnimator = DefaultItemAnimator()
            adapter = SevenDaysRecyclerViewAdapter(calendar,searchViewModel::getDeedsOfDay)
        }

        val localCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH,-6)
        }
        xmlViews.SearchDeedQueryEditText.addTextChangedListener(searchTextWatcher)
        lifecycleScope.launch {
            queryFlow.debounce(400).collectLatest{
                if (it.isNotBlank()){
                    searchViewModel.getDeedBySearchQuery(it)
                }else{
                    searchViewModel.getLastSevenDayDeeds(localCalendar.timeInMillis)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                searchViewModel.deedList.collectLatest {
                    when(it.size){
                        0-> hideDataRecyclerView()
                        else->{
                            showDataRecyclerView()
                            with(xmlViews.SearchedRecyclerView){
                                layoutManager = LinearLayoutManager(requireContext())
                                itemAnimator = DefaultItemAnimator()
                                adapter = DeedItemsListRecyclerViewAdapter(it,::launchShowDeedActivity)
                            }
                        }
                    }

                }
            }
        }


    }
    private fun setUpFilterMenus(searchDeedViewModel : SearchDeedViewModel) {
        arrayOf(xmlViews.FilterCategorySearchRecyclerView,xmlViews.FilterMoodSearchRecyclerView,
            xmlViews.FilterAttributeSearchRecyclerView).forEach {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.itemAnimator = DefaultItemAnimator()
        }

        with(xmlViews){
            FilterCategorySearchRecyclerView.adapter =
                StringSelectionRecyclerViewAdapter(CategoryEnum.entries.map { it.name }){
                    toggleVisibility(xmlViews.CategorySelectMainView)
                    FilterCategorySearchTextView.text = it
                    FilterCategorySearchTextView.setCompoundDrawablesWithIntrinsicBounds(CategoryEnum.valueOf(it).resId,0,0,0)
                    searchDeedViewModel.getDeedByCategory(it)
                }
            FilterMoodSearchRecyclerView.adapter =
                StringSelectionRecyclerViewAdapter(Emojis.entries.map { it.code }){
                    toggleVisibility(xmlViews.MoodSelectMainView)
                    FilterMoodSearchTextView.text = it
                    searchDeedViewModel.getDeedByMood(it)
                }
            FilterAttributeSearchRecyclerView.adapter =
                StringSelectionRecyclerViewAdapter(deedAttributes){
                    toggleVisibility(xmlViews.AttributeSelectMainView)
                    FilterAttributeSearchTextView.text = it
                    searchDeedViewModel.getDeedByCategory(it)
                }
        }

    }

    private fun setUpFilterSelectors() {
        with(xmlViews){
            CategorySelectCardView.setOnClickListener{
                toggleVisibility(CategorySelectMainView)
            }
            MoodSelectCardView.setOnClickListener{
                toggleVisibility(MoodSelectMainView)
            }
            AttributeSelectCardView.setOnClickListener {
                toggleVisibility(AttributeSelectMainView)
            }

            SelectCategorySelectTextView.setOnClickListener {  toggleVisibility(CategorySelectMainView) }
            MoodSelectTextView.setOnClickListener { toggleVisibility(MoodSelectMainView) }
            SortBySelectTextView.setOnClickListener { toggleVisibility(AttributeSelectMainView) }
        }
    }

    private fun toggleVisibility(view : View){
        println("CardView Visibility : ${view.visibility}")
        view.visibility = if (view.isVisible) View.GONE else View.VISIBLE
    }

    private fun showDataRecyclerView(){
        xmlViews.SearchedRecyclerView.visibility = View.VISIBLE
        xmlViews.NoDeedFoundInfoImageView.visibility = View.INVISIBLE
    }

    private fun hideDataRecyclerView(){
        xmlViews.SearchedRecyclerView.visibility = View.INVISIBLE
        xmlViews.NoDeedFoundInfoImageView.visibility = View.VISIBLE
    }

    private fun launchShowDeedActivity(deed : Deed){
        Intent(requireContext(),ShowDeedActivity::class.java).apply {
            putExtra(ExtrasNames.PARCELABLE_DEED.name,deed)
        }.also(::startActivity)
    }

    val searchTextWatcher = object  : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            lifecycleScope.launch {
                queryFlow.emit(s.toString())
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }
}