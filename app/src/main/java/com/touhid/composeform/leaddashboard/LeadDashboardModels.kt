package com.touhid.composeform.leaddashboard

enum class LeadStatusFilter(val label: String) {
    Approved("অনুমোদিত"),
    Cancelled("বাতিল"),
    Pending("পেন্ডিং"),
    EKyc("ই-কেওয়াইসি"),
}

enum class LeadStatus(val badgeLabel: String) {
    Pending("পেন্ডিং"),
    Approved("অনুমোদিত"),
    EKycSubmitted("ই-কেওয়াইসি জমা হয়েছে"),
    Cancelled("বাতিল"),
}

data class LeadPerson(val name: String, val id: String)

data class LeadCard(
    val id: String,
    val merchantName: String,
    val status: LeadStatus,
    val phoneNumber: String,
    val address: String,
    val leadOfficer: LeadPerson,
    val approver: LeadPerson? = null,
    val eKycDoneBy: String? = null,
    val cancellationReason: String? = null,
)

fun sampleLeadCards(): List<LeadCard> = listOf(
    LeadCard(
        id = "lead-1",
        merchantName = "টেস্ট মার্চেন্ট এ",
        status = LeadStatus.Pending,
        phoneNumber = "01208-567890",
        address = "২ নম্বর রোড, ব্লক-বি, সৈয়দ শাহ রোড, বাকলিয়া",
        leadOfficer = LeadPerson("Jamal Bhuiyan (A11002912)", "12930198921"),
    ),
    LeadCard(
        id = "lead-2",
        merchantName = "টেস্ট মার্চেন্ট এ",
        status = LeadStatus.Approved,
        phoneNumber = "01208-567890",
        address = "২ নম্বর রোড, ব্লক-বি, সৈয়দ শাহ রোড, বাকলিয়া",
        leadOfficer = LeadPerson("Jamal Bhuiyan (A11002912)", "12930198921"),
        approver = LeadPerson("Khastogir Alom (OM)", "12930198921"),
    ),
    LeadCard(
        id = "lead-3",
        merchantName = "টেস্ট মার্চেন্ট এ",
        status = LeadStatus.EKycSubmitted,
        phoneNumber = "01208-567890",
        address = "২ নম্বর রোড, ব্লক-বি, সৈয়দ শাহ রোড, বাকলিয়া",
        leadOfficer = LeadPerson("Jamal Bhuiyan (A11002912)", "12930198921"),
        approver = LeadPerson("Khastogir Alom (OM)", "12930198921"),
        eKycDoneBy = "Jamal Bhuiyan",
    ),
    LeadCard(
        id = "lead-4",
        merchantName = "টেস্ট মার্চেন্ট এ",
        status = LeadStatus.Cancelled,
        phoneNumber = "01208-567890",
        address = "২ নম্বর রোড, ব্লক-বি, সৈয়দ শাহ রোড, বাকলিয়া",
        leadOfficer = LeadPerson("Rahgir Bhuiyan", "12930198921"),
        approver = LeadPerson("Rahgir Alom (TM)", "Dhanmondi Outer"),
        cancellationReason = "এই লিডটি অননুমোদিত লোকেশনের জন্য ইতিমধ্যে বাতিল করা হয়েছে।",
    ),
)
