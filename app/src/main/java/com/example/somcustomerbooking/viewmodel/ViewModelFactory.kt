package com.example.somcustomerbooking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.somcustomerbooking.data.AppContainer
import com.example.somcustomerbooking.data.repository.BookingRepository


//Android normally creates a ViewModel for us. But if our ViewModel needs arguments, Android doesn't automatically know how to provide them. ViewModelFactory tells Android how to construct each ViewModel.This ViewModelFactory is used to create our ViewModels when those ViewModels need constructor parameters such as a repository, serviceId, slotId, or date.

class ViewModelFactory(
    private val repository: BookingRepository = AppContainer.bookingRepository,
    private val serviceId: String? = null,
    private val slotId: String? = null,
    private val date: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(ServiceListViewModel::class.java) ->
                ServiceListViewModel(repository) as T

            modelClass.isAssignableFrom(ServiceDetailsViewModel::class.java) ->
                ServiceDetailsViewModel(requireNotNull(serviceId), repository) as T

            modelClass.isAssignableFrom(BookingViewModel::class.java) ->
                BookingViewModel(
                    serviceId = requireNotNull(serviceId),
                    slotId = requireNotNull(slotId),
                    date = requireNotNull(date),
                    repository = repository
                ) as T

            modelClass.isAssignableFrom(MyBookingsViewModel::class.java) ->
                MyBookingsViewModel(repository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
