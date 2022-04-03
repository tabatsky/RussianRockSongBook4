package jatx.russianrocksongbook.donation.internal.viewmodel

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
    fun purchaseItem(sku: String) {
        callbacks.onPurchaseItem(sku)
    }
}