package jatx.russianrocksongbook.donation.internal.viewmodel

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import jatx.russianrocksongbook.commonviewmodel.UIAction
import javax.inject.Inject

@HiltViewModel
internal class DonationViewModel @Inject constructor(
    appStateHolder: AppStateHolder,
    commonViewModelDeps: CommonViewModelDeps
): CommonViewModel(
    appStateHolder,
    commonViewModelDeps
) {
    companion object {
        private const val key = "Donation"

        @Composable
        fun getInstance(): DonationViewModel {
            storage[key] = hiltViewModel<DonationViewModel>()
            storage[key]?.launchJobsIfNecessary()
            return storage[key] as DonationViewModel
        }
    }

    override fun handleAction(action: UIAction) {
        when (action) {
            is PurchaseItem -> purchaseItem(action.sku)
            else -> super.handleAction(action)
        }
    }

    private fun purchaseItem(sku: String) {
        callbacks.onPurchaseItem(sku)
    }
}