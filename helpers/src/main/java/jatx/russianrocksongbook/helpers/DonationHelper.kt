package jatx.russianrocksongbook.helpers

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.android.billingclient.api.*
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import java.util.*
import javax.inject.Inject

val DONATIONS = listOf(
    1, 2, 5, 10, 20, 50, 100, 200
)

val SKUS = DONATIONS.map { "donation_$it" }

val DONATIONS_LANDSCAPE = listOf(
    1, 5, 20, 100, 2, 10, 50, 200
)

val SKUS_LANDSCAPE = DONATIONS_LANDSCAPE.map { "donation_$it" }

class DonationHelper @Inject constructor(
    private val activity: Activity
): PurchasesUpdatedListener {
    private val mvvmViewModel = (activity as? ComponentActivity)?.let {
        val mvvmViewModel: MvvmViewModel by it.viewModels()
        mvvmViewModel
    }

    private var billingClient: BillingClient = BillingClient
        .newBuilder(activity)
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
                        billingClient.launchBillingFlow(activity, flowParams)
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
                    activity.runOnUiThread {
                        mvvmViewModel?.showToast(R.string.thanks_for_donation)
                    }
                }
            }
        }
    }
}