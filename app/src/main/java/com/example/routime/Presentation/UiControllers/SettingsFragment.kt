package com.example.routime.Presentation.UiControllers

import ContextViewModelFactory
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routime.CategoryEnum
import com.example.routime.Constants
import com.example.routime.Data.Models.Category
import com.example.routime.Presentation.Services.HourlyReminderService
import com.example.routime.Presentation.UiAdapters.CategoryItemsRecyclerViewAdapter
import com.example.routime.Presentation.UiDialogs.AddCategoryDialogFragment
import com.example.routime.Presentation.ViewModels.SettingsViewModel
import com.example.routime.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private lateinit var xmlView : FragmentSettingsBinding
    private lateinit var sharedPreference : SharedPreferences
    private var selectedReminderFrequency = 0
    private var selectedInactivityTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreference = requireContext().getSharedPreferences(Constants.SHARED_PREF_NAME,Activity.MODE_PRIVATE)

        xmlView = FragmentSettingsBinding.inflate(inflater,container,false)
        return xmlView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsViewModel : SettingsViewModel by viewModels {
            ContextViewModelFactory(requireContext().applicationContext)
        }

        val defaultCategories = CategoryEnum.entries.map {
            Category(it.name,"Default",it.resId)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                settingsViewModel.categoryList.collectLatest {
                    with(xmlView.CategoriesRecyclerView){
                        layoutManager = LinearLayoutManager(requireContext())
                        itemAnimator = DefaultItemAnimator()
                        adapter = CategoryItemsRecyclerViewAdapter(defaultCategories.plus(it))
                    }
                }
            }
        }



        xmlView.AddNewCategoryButton.setOnClickListener {
            val addCategoryDialogFragment = AddCategoryDialogFragment().apply {
                isCancelable = false
                setOnAddNewCategoryListener {category->
                    settingsViewModel.insertNewCategory(category)
                    true
                }

            }
            addCategoryDialogFragment.show(parentFragmentManager,"AddCategoryFragment")
        }

        xmlView.EnableReminderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleReminderFields(isChecked)
        }

        xmlView.EnableInactivityReminderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleInactivityReminderFields(isChecked)
        }

        val hoursArray = arrayOf(1,3,5,12,24)
        val hoursStringArray = arrayOf("1 Hour","3 Hours","5 Hours","12 Hours","1 Time Reminder")
        val basicArrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,hoursStringArray).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        xmlView.ReminderFrequencySpinner.run {
            adapter = basicArrayAdapter
            setSelection(1)
            onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setSelection(position)
                    selectedReminderFrequency = hoursArray[position]
                    println("SelectedReminderFrequency : $selectedReminderFrequency Hours")
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        val startTimePickerDialog = TimePickerDialog(requireContext(),{_,selectedHour,selectedMinutes->
            xmlView.StartTimeOfReminderTextView.text = "${"%02d".format(selectedHour)}:00"
        },0,0,true)
        xmlView.StartTimeOfReminderTextView.setOnClickListener { startTimePickerDialog.show() }

        val endTimePickerDialog = TimePickerDialog(requireContext(),{_,selectedHour,selectedMinutes->
            xmlView.EndTimeOfReminderTextView.text = "${"%02d".format(selectedHour)}:00"
        },0,0,true)
        xmlView.EndTimeOfReminderTextView.setOnClickListener { endTimePickerDialog.show() }


        xmlView.TimeOfInactivityReminderSpinner.run {
            adapter = basicArrayAdapter
            setSelection(0)
            onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setSelection(position)
                    selectedInactivityTime = hoursArray[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        }

        setUpSettingsPreferences()

        xmlView.SaveSettingsButton.setOnClickListener {
            val startReminderHour = xmlView.StartTimeOfReminderTextView.text.toString().substringBefore(':').toInt()
            val endReminderHour = xmlView.EndTimeOfReminderTextView.text.toString().substringBefore(':').toInt()
            if (endReminderHour <= startReminderHour){
                Toast.makeText(requireContext(),"End Time must be bigger than Start Time",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Settings.canDrawOverlays(requireContext())){
                Toast.makeText(requireContext(),"You Need To Enable Overlay To Receive Reminders",Toast.LENGTH_SHORT).show()
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:${requireContext().packageName}".toUri()).also(requireContext()::startActivity)
                return@setOnClickListener
            }

            xmlView.SaveSettingsButton.apply {
                text = "Saving .."
                isEnabled = false
            }

            val sharedPreferenceEditor = sharedPreference.edit()

            sharedPreferenceEditor.putBoolean(Constants.REMINDER_ENABLED,xmlView.EnableReminderSwitch.isChecked)
            if (xmlView.EnableReminderSwitch.isChecked){
                with(sharedPreferenceEditor){
                    putInt(Constants.REMINDER_FREQUENCY,selectedReminderFrequency)
                    putInt(Constants.REMINDER_START_TIME,startReminderHour)
                    putInt(Constants.REMINDER_END_TIME,endReminderHour)
                    putBoolean(Constants.REMINDER_WEEKDAYS_ONLY,xmlView.RemindOnWeekDaysOnlySwitch.isChecked)
                    putBoolean(Constants.REMINDER_NOTIFICATION_SOUND,xmlView.ReminderSoundSwitch.isChecked)
                    putBoolean(Constants.REMINDER_NOTIFICATION_VIBRATE,xmlView.ReminderVibrationSwitch.isChecked)
                }
            }

            sharedPreferenceEditor.putBoolean(Constants.INACTIVITY_REMINDER_ENABLED,xmlView.EnableInactivityReminderSwitch.isChecked)
            if (xmlView.EnableInactivityReminderSwitch.isChecked){
                with(sharedPreferenceEditor){
                    putInt(Constants.INACTIVITY_REMINDER_POST_TIME,selectedInactivityTime)
                    putString(Constants.INACTIVITY_REMINDER_MESSAGE,xmlView.CustomInactivityReminderMessageTextView.text.toString())
                    putBoolean(Constants.INACTIVITY_REMINDER_SOUND_ENABLED,xmlView.PlayInactivityReminderSoundSwitch.isChecked)
                }
            }

            sharedPreferenceEditor.putInt("TEST_COUNTER",0)
            sharedPreferenceEditor.apply()
            xmlView.SaveSettingsButton.text = "Saved Successfully"
            Intent(requireContext(),HourlyReminderService::class.java).also(requireContext()::startService)
            (activity as NavigatorMainActivity).navigateToHomeFragment()

        }
    }

    private fun setUpSettingsPreferences(){
        val reminderEnabled = sharedPreference.getBoolean(Constants.REMINDER_ENABLED,false).also(::println)
        val reminderFrequency = sharedPreference.getInt(Constants.REMINDER_FREQUENCY,1).also(::println)
        val reminderStartTime = sharedPreference.getInt(Constants.REMINDER_START_TIME,0).also(::println)
        val reminderEndTime = sharedPreference.getInt(Constants.REMINDER_END_TIME,0).also(::println)
        val reminderWeekDaysOnly = sharedPreference.getBoolean(Constants.REMINDER_WEEKDAYS_ONLY,false).also(::println)
        val reminderNotificationSoundEnabled = sharedPreference.getBoolean(Constants.REMINDER_NOTIFICATION_SOUND,false).also(::println)
        val reminderNotificationVibratesEnabled = sharedPreference.getBoolean(Constants.REMINDER_NOTIFICATION_VIBRATE,false).also(::println)
        val inactivityReminderEnabled = sharedPreference.getBoolean(Constants.INACTIVITY_REMINDER_ENABLED,false).also(::println)
        val inactivityTriggerTime = sharedPreference.getInt(Constants.INACTIVITY_REMINDER_POST_TIME,0).also(::println)
        val customMessage = "We miss you! Come back and log your good deeds \uD83C\uDF1F"
        val inactivityMessage = sharedPreference.getString(Constants.INACTIVITY_REMINDER_MESSAGE,customMessage).also(::println)
        val inactivityReminderSoundEnabled = sharedPreference.getBoolean(Constants.INACTIVITY_REMINDER_SOUND_ENABLED,false).also(::println)

        val hoursArray = arrayOf(1,3,5,12,24)
        with(xmlView){
            EnableReminderSwitch.isChecked = reminderEnabled
            ReminderFrequencySpinner.setSelection(hoursArray.indexOf(reminderFrequency))
            StartTimeOfReminderTextView.text = "${"%02d".format(reminderStartTime)}:00"
            EndTimeOfReminderTextView.text = "${"%02d".format(reminderEndTime)}:00"
            RemindOnWeekDaysOnlySwitch.isChecked = reminderWeekDaysOnly
            ReminderSoundSwitch.isChecked = reminderNotificationSoundEnabled
            ReminderVibrationSwitch.isChecked = reminderNotificationVibratesEnabled

            EnableInactivityReminderSwitch.isChecked = inactivityReminderEnabled
            TimeOfInactivityReminderSpinner.setSelection(hoursArray.indexOf(inactivityTriggerTime))
            CustomInactivityReminderMessageTextView.setText(inactivityMessage)
            PlayInactivityReminderSoundSwitch.isChecked = inactivityReminderSoundEnabled
        }
    }


    private fun toggleReminderFields(isChecked : Boolean){
        val backgroundColor = ColorStateList.valueOf((if (isChecked) "#66FFFFFF" else "#66FFFFFF").toColorInt())
        with(xmlView){
            ReminderFrequencySpinner.isEnabled = isChecked
            StartTimeOfReminderTextView.isEnabled = isChecked
            EndTimeOfReminderTextView.isEnabled = isChecked
            RemindOnWeekDaysOnlySwitch.isEnabled = isChecked
            ReminderSoundSwitch.isEnabled = isChecked
            ReminderVibrationSwitch.isEnabled = isChecked

            ReminderFrequencySpinner.backgroundTintList = backgroundColor
            StartTimeOfReminderTextView.backgroundTintList = backgroundColor
            EndTimeOfReminderTextView.backgroundTintList = backgroundColor
        }

    }

    private fun toggleInactivityReminderFields(isChecked : Boolean){
        val backgroundColor = ColorStateList.valueOf((if (isChecked) "#66FFFFFF" else "#66FFFFFF").toColorInt())
        with(xmlView){
            TimeOfInactivityReminderSpinner.isEnabled = isChecked
            CustomInactivityReminderMessageTextView.isEnabled = isChecked
            PlayInactivityReminderSoundSwitch.isEnabled = isChecked

            TimeOfInactivityReminderSpinner.backgroundTintList = backgroundColor
            CustomInactivityReminderMessageTextView.backgroundTintList = backgroundColor
        }

    }

}