package jatx.russianrocksongbook.purchase

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import java.util.ArrayList
import javax.inject.Inject

val DONATIONS = listOf(
    1, 2, 5, 10, 20, 50, 100, 200
)

val SKUS = DONATIONS.map { "donation_$it" }

class DonationHelper @Inject constructor(
    val mainActivity: Activity,
    val mvvmViewModel: MvvmViewModel
): PurchasesUpdatedListener {
    private var billingClient: BillingClient = BillingClient
        .newBuilder(mainActivity)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    checkDonations()
                } else {
                    Log.e("response code", billingResult.responseCode.toString())
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.e("billing service", "disconnected")
            }
        })
    }

    fun purchaseItem(sku: String) {
        val skuList = ArrayList<String>()
        skuList.add(sku)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(params.build()) { billingResult, mutableList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && mutableList != null) {
                Log.e("mutableList", mutableList.toString())
                mutableList.forEach {
                    if (it.sku == sku) {
                        Log.e("billing flow", "launching")
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(it)
                            .build()
                        billingClient.launchBillingFlow(mainActivity, flowParams)
                    }
                }
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        mutableList: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && mutableList != null) {
            Log.e("mutableList0", mutableList.toString())
            mutableList.forEach {
                consumePurchase(it)
            }
        }
    }

    private fun checkDonations() {
        billingClient.queryPurchasesAsync(
            BillingClient.SkuType.INAPP
        ) { _, list ->
            list.forEach {
                consumePurchase(it)
            }
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        for (sku in purchase.skus) {
            if (sku in SKUS) {
                val consumeParams = ConsumeParams
                    .newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.consumeAsync(consumeParams) { responseCode, _ ->
                    Log.e("consume", responseCode.responseCode.toString())
                    mainActivity.runOnUiThread {
                        mvvmViewModel.showToast(R.string.thanks_for_donation)
                    }
                }
            }
        }
    }
}