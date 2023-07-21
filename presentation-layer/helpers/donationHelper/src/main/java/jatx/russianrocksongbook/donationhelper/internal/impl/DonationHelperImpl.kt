package jatx.russianrocksongbook.donationhelper.internal.impl

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.donationhelper.api.DonationHelper
import jatx.russianrocksongbook.donationhelper.api.SKUS
import jatx.russianrocksongbook.donationhelper.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import java.util.*
import javax.inject.Inject

@ActivityScoped
@BoundTo(supertype = DonationHelper::class, component = ActivityComponent::class)
internal class DonationHelperImpl @Inject constructor(
    private val activity: Activity
): PurchasesUpdatedListener, DonationHelper {
    private val commonViewModel by lazy {
        CommonViewModel.getStoredInstance()
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

    override fun purchaseItem(sku: String) {
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
                        if (responseCode.responseCode == 0) {
                            commonViewModel?.showToast(R.string.thanks_for_donation)
                        }
                    }
                }
            }
        }
    }
}