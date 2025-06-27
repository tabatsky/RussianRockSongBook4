package jatx.russianrocksongbook.donationhelper.internal.impl

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.donationhelper.api.DonationHelper
import jatx.russianrocksongbook.donationhelper.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.ShowToastWithResource
import javax.inject.Inject

@ActivityScoped
@BoundTo(supertype = DonationHelper::class, component = ActivityComponent::class)
class DonationHelperImpl @Inject constructor(
    private val activity: Activity
): PurchasesUpdatedListener, DonationHelper {
    private val commonViewModel by lazy {
        CommonViewModel.getStoredInstance()
    }

    private var billingClient: BillingClient = BillingClient
        .newBuilder(activity)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
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
        val productList = listOf(
            Product.newBuilder()
                .setProductId(sku)
                .setProductType(ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, mutableList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.e("mutableList", mutableList.toString())
                mutableList.forEach {
                    val productDetailsParamsList = arrayListOf<ProductDetailsParams>()
                    if (it.productId == sku && it.productType == ProductType.INAPP) {
                        Log.e("billing flow", "launching")
                        val detailParams = ProductDetailsParams
                            .newBuilder()
                            .setProductDetails(it)
                            .build()
                        productDetailsParamsList.add(detailParams)
                    }

                    if (productDetailsParamsList.isNotEmpty()) {
                        val flowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
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
        val queryPurchasesParams = QueryPurchasesParams
            .newBuilder()
            .setProductType(ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(
            queryPurchasesParams
        ) { _, list ->
            list.forEach {
                consumePurchase(it)
            }
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        for (product in purchase.products) {
            val consumeParams = ConsumeParams
                .newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.consumeAsync(consumeParams) { responseCode, _ ->
                Log.e("consume", responseCode.responseCode.toString())
                activity.runOnUiThread {
                    if (responseCode.responseCode == 0) {
                        commonViewModel?.submitEffect(
                            ShowToastWithResource(R.string.thanks_for_donation)
                        )
                    }
                }
            }
        }
    }
}