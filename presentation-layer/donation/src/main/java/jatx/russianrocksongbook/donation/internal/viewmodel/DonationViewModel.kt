package jatx.russianrocksongbook.donation.internal.viewmodel

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.russianrocksongbook.viewmodel.CommonViewModelDeps
import javax.inject.Inject

@HiltViewModel
internal class DonationViewModel @Inject constructor(
    commonStateHolder: CommonStateHolder,
    commonViewModelDeps: CommonViewModelDeps
): CommonViewModel(
    commonStateHolder,
    commonViewModelDeps
) {
    companion object {
        private const val key = "Donation"

        @Composable
        fun getInstance(): DonationViewModel {
            if (!storage.containsKey(key)){
                storage[key] = hiltViewModel<DonationViewModel>()
            }
            return storage[key] as DonationViewModel
        }
    }

    fun purchaseItem(sku: String) {
        callbacks.onPurchaseItem(sku)
    }
}