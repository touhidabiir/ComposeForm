package com.touhid.composeform.feature.acquisition.data

import com.touhid.composeform.feature.acquisition.domain.AcquisitionAudit
import com.touhid.composeform.feature.acquisition.domain.AcquisitionDetail
import com.touhid.composeform.feature.acquisition.domain.AcquisitionImages
import com.touhid.composeform.feature.acquisition.domain.AcquisitionListItem
import com.touhid.composeform.feature.acquisition.domain.AcquisitionListPage
import com.touhid.composeform.feature.acquisition.domain.AcquisitionReason
import com.touhid.composeform.feature.acquisition.domain.ContactInfo
import com.touhid.composeform.feature.acquisition.domain.ContactPerson
import com.touhid.composeform.feature.acquisition.domain.DigitalPayment
import com.touhid.composeform.feature.acquisition.domain.Facility
import com.touhid.composeform.feature.acquisition.domain.OutletInfo
import com.touhid.composeform.feature.acquisition.domain.PremiumnessScoreRange
import com.touhid.composeform.feature.acquisition.domain.SurveyResponse
import com.touhid.composeform.feature.acquisition.domain.WalletInfo
import com.touhid.composeform.lead.data.toDomain

internal fun AcquisitionListPageDto.toDomain() = AcquisitionListPage(
    count = count,
    pageNo = pageNo,
    pageSize = pageSize,
    totalPages = totalPages,
    results = results.map { it.toDomain() },
)

internal fun AcquisitionListItemDto.toDomain() = AcquisitionListItem(
    id = id,
    displayId = displayId,
    shopName = shopName,
    walletNumber = walletNumber,
    address = address,
    leadCloser = leadCloser.toDomain(),
    submittedAt = submittedAt,
    canReview = canReview,
    rejectionCount = rejectionCount,
)

internal fun AcquisitionDetailDto.toDomain() = AcquisitionDetail(
    id = id,
    displayId = displayId,
    shopName = shopName,
    walletNumber = walletNumber,
    status = status,
    premiumnessScore = premiumnessScore,
    premiumnessScoreRanges = premiumnessScoreRanges.map { it.toDomain() },
    images = images.toDomain(),
    outletInfo = outletInfo.toDomain(),
    digitalPayment = digitalPayment.toDomain(),
    contactInfo = contactInfo.toDomain(),
    walletInfo = walletInfo.toDomain(),
    surveyResponses = surveyResponses.map { it.toDomain() },
    audit = audit.toDomain(),
    rejectionReasons = rejectionReasons?.map { it.toDomain() },
)

internal fun PremiumnessScoreRangeDto.toDomain() = PremiumnessScoreRange(
    minScore = minScore,
    maxScore = maxScore,
    isActive = isActive,
    color = color,
)

internal fun AcquisitionImagesDto.toDomain() = AcquisitionImages(
    shopImageOutside = shopImageOutside,
    shopImageInside = shopImageInside,
    businessProofImage = businessProofImage,
)

internal fun OutletInfoDto.toDomain() = OutletInfo(
    address = address,
    district = district,
    thana = thana,
    marketName = marketName,
    bmccCode = bmccCode,
    bmccName = bmccName,
    productType = productType,
    outletLocationType = outletLocationType,
    outletType = outletType,
)

internal fun DigitalPaymentDto.toDomain() = DigitalPayment(
    cardPaymentAvailable = cardPaymentAvailable,
    otherMfsAvailable = otherMfsAvailable,
    facilities = facilities.map { Facility(name = it.name, completed = it.completed) },
)

internal fun ContactInfoDto.toDomain() = ContactInfo(
    contactPerson = contactPerson.toDomain(),
    outletOwner = outletOwner.toDomain(),
)

internal fun ContactPersonDto.toDomain() = ContactPerson(
    name = name,
    phoneNumber = phoneNumber,
    designation = designation,
)

internal fun WalletInfoDto.toDomain() = WalletInfo(
    proposedWalletNumber = proposedWalletNumber,
    simStaysAtOutlet = simStaysAtOutlet,
    simUsedInSmartphone = simUsedInSmartphone,
    simOwnedByShopOwner = simOwnedByShopOwner,
)

internal fun SurveyResponseDto.toDomain() = SurveyResponse(
    question = question,
    answer = answer,
    points = points,
)

internal fun AcquisitionAuditDto.toDomain() = AcquisitionAudit(
    createdAt = createdAt,
    submittedAt = submittedAt,
    submittedBy = submittedBy.toDomain(),
)

internal fun AcquisitionReasonDto.toDomain() = AcquisitionReason(
    id = id,
    reason = reason,
    isNoteRequired = isNoteRequired,
)
