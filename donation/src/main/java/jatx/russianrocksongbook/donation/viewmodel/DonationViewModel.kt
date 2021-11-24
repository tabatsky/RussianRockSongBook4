package jatx.russianrocksongbook.donation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import jatx.russianrocksongbook.viewmodel.ViewModelParam
import javax.inject.Inject

@HiltViewModel
class DonationViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    commonStateHolder: CommonStateHolder
): MvvmViewModel(
    viewModelParam,
    commonStateHolder
) {
    fun purchaseItem(sku: String) {
        callbacks.onPurchaseItem(sku)
    }
}