package jatx.russianrocksongbook.donation.internal.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import javax.inject.Inject

@HiltViewModel
internal class DonationViewModel @Inject constructor(
    viewModelDeps: ViewModelDeps,
    commonStateHolder: CommonStateHolder
): MvvmViewModel(
    viewModelDeps,
    commonStateHolder
) {
    fun purchaseItem(sku: String) {
        callbacks.onPurchaseItem(sku)
    }
}