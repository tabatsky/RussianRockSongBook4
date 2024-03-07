package jatx.russianrocksongbook.donationhelper.api

val DONATIONS = listOf(
    1, 2, 5, 10, 20, 50, 100, 200
)

val SKUS = DONATIONS.map { "donation_$it" }

val DONATIONS_LANDSCAPE = listOf(
    1, 5, 20, 100, 2, 10, 50, 200
)

val SKUS_LANDSCAPE = DONATIONS_LANDSCAPE.map { "donation_$it" }

interface DonationHelper {
    fun purchaseItem(sku: String)
}