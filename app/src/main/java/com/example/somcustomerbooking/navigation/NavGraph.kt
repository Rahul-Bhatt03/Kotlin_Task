package com.example.somcustomerbooking.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sombooking.ui.screens.booking.BookingScreen
import com.example.sombooking.ui.screens.bookings.MyBookingsScreen
import com.example.sombooking.ui.screens.service.ServiceDetailsScreen
import com.example.sombooking.ui.screens.service.ServiceListScreen

@Composable
fun SomBookingNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.SERVICE_LIST) {

        composable(Screen.SERVICE_LIST) {
            ServiceListScreen(
                onServiceClick = { serviceId ->
                    navController.navigate(Screen.serviceDetails(serviceId))
                },
                onMyBookingsClick = {
                    navController.navigate(Screen.MY_BOOKINGS)
                }
            )
        }

        composable(
            route = Screen.SERVICE_DETAILS_ROUTE,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = requireNotNull(backStackEntry.arguments?.getString("serviceId"))
            ServiceDetailsScreen(
                serviceId = serviceId,
                onContinueToBooking = { date, slotId ->
                    navController.navigate(Screen.booking(serviceId, date, slotId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BOOKING_ROUTE,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("slotId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments!!
            BookingScreen(
                serviceId = requireNotNull(args.getString("serviceId")),
                date = requireNotNull(args.getString("date")),
                slotId = requireNotNull(args.getString("slotId")),
                onBack = { navController.popBackStack() },
                onDone = {
                    // Clear the Service Details + Booking screens from the
                    // back stack so "back" from My Bookings returns to the
                    // Service List, not to a completed booking form.
                    navController.navigate(Screen.MY_BOOKINGS) {
                        popUpTo(Screen.SERVICE_LIST) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.MY_BOOKINGS) {
            MyBookingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
