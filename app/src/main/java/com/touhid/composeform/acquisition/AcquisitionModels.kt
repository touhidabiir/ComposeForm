package com.touhid.composeform.acquisition

data class AcquisitionPerson(val name: String, val idOrPhone: String)

data class AcquisitionListItem(
    val id: String,
    val merchantName: String,
    val phoneNumber: String,
    val address: String,
    val leadOfficer: AcquisitionPerson,
)

fun sampleAcquisitionListItems(): List<AcquisitionListItem> = listOf(
    AcquisitionListItem(
        id = "acq-1",
        merchantName = "টেস্ট মার্চেন্ট এ",
        phoneNumber = "01208-567890",
        address = "২ নম্বর রোড, ব্লক-বি, সৈয়দ শাহ রোড, বাকলিয়া",
        leadOfficer = AcquisitionPerson("Jamal Bhuiyan (A11002912)", "12930198921"),
    ),
    AcquisitionListItem(
        id = "acq-2",
        merchantName = "টেস্ট মার্চেন্ট এ",
        phoneNumber = "01208-567890",
        address = "২ নম্বর রোড, ব্লক-বি, সৈয়দ শাহ রোড, বাকলিয়া",
        leadOfficer = AcquisitionPerson("Jamal Bhuiyan (A11002912)", "12930198921"),
    ),
)

data class LabeledPair(val label: String, val value: String)

data class AcquisitionDetail(
    val merchantName: String,
    val phoneNumber: String,
    val score: Int,
    val maxScore: Int,
    val photoCaptions: List<String>,
    val shopOwner: AcquisitionPerson,
    val shopOperator: AcquisitionPerson,
    val outletInfo: List<LabeledPair>,
    val walletInfo: List<LabeledPair>,
)

fun sampleAcquisitionDetail(): AcquisitionDetail = AcquisitionDetail(
    merchantName = "টেস্ট মার্চেন্ট এ",
    phoneNumber = "01113-783737",
    score = 89,
    maxScore = 100,
    photoCaptions = listOf(
        "আউটলেটের বাহিরের ছবি",
        "আউটলেটের ভিতরের ছবি",
        "ব্যবসার পরিচয়পত্রের ছবি",
    ),
    shopOwner = AcquisitionPerson("Raja Ahmed Shetu", "01723-456-789"),
    shopOperator = AcquisitionPerson("Kalam Bashir", "01723-456-789"),
    outletInfo = listOf(
        LabeledPair("BWCC", "5002"),
        LabeledPair("Product Type", "Merchant Plus Lite B"),
        LabeledPair("Outlet location type", "বহুতল ভবন"),
        LabeledPair("Outlet ambiance", "খুব ভালো"),
        LabeledPair("Card payment available", "Yes"),
        LabeledPair("Other MFS payment available", "No"),
    ),
    walletInfo = listOf(
        LabeledPair("Proposed Wallet Number", "01723-456-789"),
        LabeledPair("SIM shops outlet?", "হ্যাঁ"),
        LabeledPair("SIM used in card/mobile?", "হ্যাঁ"),
        LabeledPair("SIM owned by Shop Owner?", "হ্যাঁ"),
    ),
)
