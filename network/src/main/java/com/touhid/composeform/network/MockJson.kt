package com.touhid.composeform.network

// Canned responses for MockDataInterceptor - the same fixture data the UI previously carried
// locally (as sample JSON parsed directly in :app) before the lead/acquisition endpoints were
// wired up. Kept here now that :network is the one short-circuiting the real (still-nonexistent)
// backend, rather than :app bypassing the network layer.
// TODO: delete this whole file once a real backend is live - see MockDataInterceptor.kt, the only
// consumer of this object.
internal object MockJson {

    val LEAD_DASHBOARD = """
    {
      "data": {
        "count": 150,
        "page_no": 1,
        "page_size": 20,
        "total_pages": 8,
        "results": [
          {
            "id": 100238471,
            "display_id": "LEAD-2026-100238471",
            "shop_name": "Romij Electric",
            "wallet_number": "01723456789",
            "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
            "status": "pending",
            "premiumness_score": 61.2,
            "can_submit_ekyc": false,
            "lead_closer": {
              "name": "Jamal Bhuiyan",
              "employee_id": "A11002912",
              "whitelisting_number": "01930119876",
              "serving_ma": "01930198765"
            },
            "reviewer": null,
            "ekyc_submitter": null,
            "rejection": null,
            "created_at": "2026-07-15T10:30:00+06:00"
          },
          {
            "id": 100238472,
            "display_id": "LEAD-2026-100238472",
            "shop_name": "Test Merchant A",
            "wallet_number": "01208567890",
            "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
            "status": "approved",
            "premiumness_score": 72.4,
            "can_submit_ekyc": true,
            "lead_closer": {
              "name": "Jamal Bhuiyan",
              "employee_id": "A11002912",
              "whitelisting_number": "01930119876",
              "serving_ma": "01930198765"
            },
            "reviewer": {
              "name": "Khastogir Alom",
              "designation": "OM",
              "territory": "Bakalia"
            },
            "ekyc_submitter": null,
            "rejection": null,
            "created_at": "2026-07-14T09:05:00+06:00"
          },
          {
            "id": 100238473,
            "display_id": "LEAD-2026-100238473",
            "shop_name": "Test Merchant A",
            "wallet_number": "01208567890",
            "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
            "status": "approved",
            "premiumness_score": 84.9,
            "can_submit_ekyc": false,
            "lead_closer": {
              "name": "Jamal Bhuiyan",
              "employee_id": "A11002912",
              "whitelisting_number": "01930119876",
              "serving_ma": "01930198765"
            },
            "reviewer": {
              "name": "Khastogir Alom",
              "designation": "OM",
              "territory": "Bakalia"
            },
            "ekyc_submitter": {
              "name": "Jamal Bhuiyan"
            },
            "rejection": null,
            "created_at": "2026-07-10T14:45:00+06:00"
          },
          {
            "id": 100238474,
            "display_id": "LEAD-2026-100238474",
            "shop_name": "Test Merchant A",
            "wallet_number": "01208567890",
            "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
            "status": "rejected",
            "premiumness_score": 38.0,
            "can_submit_ekyc": false,
            "lead_closer": {
              "name": "Rahgir Bhuiyan",
              "employee_id": "A11002913",
              "whitelisting_number": "12930198921",
              "serving_ma": "01930198766"
            },
            "reviewer": {
              "name": "Rahgir Alom",
              "designation": "TM",
              "territory": "Dhanmondi Outer"
            },
            "ekyc_submitter": null,
            "rejection": {
              "reason": "এই লিডটি অননুমোদিত লোকেশনের জন্য ইতিমধ্যে বাতিল করা হয়েছে।"
            },
            "created_at": "2026-07-01T11:00:00+06:00"
          },
          {
            "id": 100238475,
            "display_id": "LEAD-2026-100238475",
            "shop_name": "Anowar Traders",
            "wallet_number": "01812345678",
            "address": "5 No. Road, Block-C, CDA Avenue, Bakalia",
            "status": "pending",
            "premiumness_score": 55.6,
            "can_submit_ekyc": false,
            "lead_closer": {
              "name": "Jamal Bhuiyan",
              "employee_id": "A11002912",
              "whitelisting_number": "01930119876",
              "serving_ma": "01930198765"
            },
            "reviewer": null,
            "ekyc_submitter": null,
            "rejection": null,
            "created_at": "2026-07-16T12:00:00+06:00"
          },
          {
            "id": 100238476,
            "display_id": "LEAD-2026-100238476",
            "shop_name": "Bismillah Store",
            "wallet_number": "01912345678",
            "address": "8 No. Road, Block-D, Chatteswari Road, Bakalia",
            "status": "approved",
            "premiumness_score": 68.1,
            "can_submit_ekyc": true,
            "lead_closer": {
              "name": "Jamal Bhuiyan",
              "employee_id": "A11002912",
              "whitelisting_number": "01930119876",
              "serving_ma": "01930198765"
            },
            "reviewer": {
              "name": "Khastogir Alom",
              "designation": "OM",
              "territory": "Bakalia"
            },
            "ekyc_submitter": null,
            "rejection": null,
            "created_at": "2026-07-13T09:20:00+06:00"
          },
          {
            "id": 100238477,
            "display_id": "LEAD-2026-100238477",
            "shop_name": "Karim General Store",
            "wallet_number": "01612345678",
            "address": "12 No. Road, Block-E, Pahartali, Bakalia",
            "status": "approved",
            "premiumness_score": 91.3,
            "can_submit_ekyc": false,
            "lead_closer": {
              "name": "Jamal Bhuiyan",
              "employee_id": "A11002912",
              "whitelisting_number": "01930119876",
              "serving_ma": "01930198765"
            },
            "reviewer": {
              "name": "Khastogir Alom",
              "designation": "OM",
              "territory": "Bakalia"
            },
            "ekyc_submitter": {
              "name": "Jamal Bhuiyan"
            },
            "rejection": null,
            "created_at": "2026-07-09T16:15:00+06:00"
          },
          {
            "id": 100238478,
            "display_id": "LEAD-2026-100238478",
            "shop_name": "Nasrin Fashion House",
            "wallet_number": "01512345678",
            "address": "20 No. Road, Block-F, Agrabad, Bakalia",
            "status": "rejected",
            "premiumness_score": 30.5,
            "can_submit_ekyc": false,
            "lead_closer": {
              "name": "Rahgir Bhuiyan",
              "employee_id": "A11002913",
              "whitelisting_number": "12930198921",
              "serving_ma": "01930198766"
            },
            "reviewer": {
              "name": "Rahgir Alom",
              "designation": "TM",
              "territory": "Dhanmondi Outer"
            },
            "ekyc_submitter": null,
            "rejection": {
              "reason": "প্রয়োজনীয় নথিপত্র সঠিকভাবে জমা দেওয়া হয়নি বলে লিডটি বাতিল করা হয়েছে।"
            },
            "created_at": "2026-06-28T15:30:00+06:00"
          }
        ]
      }
    }
    """.trimIndent()

    val ACQUISITION_LIST = """
    {
      "data": {
        "count": 45,
        "page_no": 1,
        "page_size": 20,
        "total_pages": 3,
        "results": [
          {
            "id": 100238471,
            "display_id": "LEAD-2026-100238471",
            "shop_name": "Romij Electric",
            "wallet_number": "01723456789",
            "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
            "lead_closer": {
              "name": "Jamal Bhuiyan",
              "employee_id": "A11002912",
              "whitelisting_number": "01930119876",
              "serving_ma": "01930198765"
            },
            "submitted_at": "2026-07-13T14:30:00+06:00",
            "can_review": true
          },
          {
            "id": 100238472,
            "display_id": "LEAD-2026-100238472",
            "shop_name": "Anowar Traders",
            "wallet_number": "01812345678",
            "address": "5 No. Road, Block-C, CDA Avenue, Bakalia",
            "lead_closer": {
              "name": "Jamal Bhuiyan",
              "employee_id": "A11002912",
              "whitelisting_number": "01930119876",
              "serving_ma": "01930198765"
            },
            "submitted_at": "2026-07-12T11:15:00+06:00",
            "can_review": true
          }
        ]
      }
    }
    """.trimIndent()

    val ACQUISITION_DETAIL = """
    {
      "is_error": false,
      "message": "success",
      "data": {
        "id": 100238471,
        "display_id": "LEAD-2026-100238471",
        "shop_name": "Romij Electric",
        "wallet_number": "01723456789",
        "status": "submitted",
        "premiumness_score": 71.25,
        "premiumness_score_ranges": [
          {
            "min_score": 0,
            "max_score": 12,
            "is_active": false,
            "color": "#E5BDB8"
          },
          {
            "min_score": 12,
            "max_score": 25,
            "is_active": false,
            "color": "#EDD1B6"
          },
          {
            "min_score": 25,
            "max_score": 37,
            "is_active": false,
            "color": "#F0E3B8"
          },
          {
            "min_score": 37,
            "max_score": 62,
            "is_active": false,
            "color": "#60AB9B"
          },
          {
            "min_score": 62,
            "max_score": 100,
            "is_active": true,
            "color": "#B4D7BF"
          }
        ],
        "images": {
          "shop_image_outside": "https://picsum.photos/id/1011/800/600",
          "shop_image_inside": "https://picsum.photos/id/1012/800/600",
          "business_proof_image": "https://picsum.photos/id/1013/800/600"
        },
        "outlet_info": {
          "address": "2 No. Road, Block-B, Syed Shah Road, Bakalia",
          "district": "Chattogram",
          "thana": "Bakalia",
          "market_name": "Avengers Tower",
          "bmcc_code": "5002",
          "bmcc_name": "Hardware & Electronics",
          "product_type": "Merchant Plus Lite A",
          "outlet_location_type": "Roadside",
          "outlet_type": "Semi-permanent"
        },
        "digital_payment": {
          "card_payment_available": true,
          "other_mfs_available": false,
          "facilities": [
            {"name": "Card payment", "completed": true}
          ]
        },
        "contact_info": {
          "contact_person": {
            "name": "Kalam Bashir",
            "phone_number": "01723456789",
            "designation": "Manager"
          },
          "outlet_owner": {
            "name": "Raju Ahmed Shetu",
            "phone_number": "01723456789"
          }
        },
        "wallet_info": {
          "proposed_wallet_number": "01723456789",
          "sim_stays_at_outlet": true,
          "sim_used_in_smartphone": true,
          "sim_owned_by_shop_owner": true
        },
        "survey_responses": [
          {"question": "Is the outlet inside a market?", "answer": "No", "points": 6.4},
          {"question": "Is it air-conditioned?", "answer": "Yes", "points": 0.0},
          {"question": "Entrance door type?", "answer": "Glass", "points": 12.6},
          {"question": "Illuminated signboard?", "answer": "Yes", "points": null},
          {"question": "Is the signboard clean?", "answer": "Yes", "points": 10.2},
          {"question": "Tube lights count?", "answer": "4-6", "points": 5.1},
          {"question": "Spot lights count?", "answer": "6-10", "points": 8.0},
          {"question": "Separate cash counter?", "answer": "Yes", "points": 3.2},
          {"question": "Accepts card payments?", "answer": "No", "points": 0.0},
          {"question": "Provides printed bills?", "answer": "Yes", "points": 20.7}
        ],
        "audit": {
          "created_at": "2026-07-15T10:30:00+06:00",
          "submitted_at": "2026-07-15T10:35:00+06:00",
          "submitted_by": {
            "name": "Jamal Bhuiyan",
            "employee_id": "A11002912",
            "whitelisting_number": "1930119876",
            "serving_ma": "1930198765"
          }
        }
      }
    }
    """.trimIndent()

    // Placeholder copy - real reason text (for both accept and reject) should be confirmed
    // against the actual backend once it exists; the accept list mirrors the sample payload
    // provided for this endpoint, the reject list is authored in a similar register/length.
    val ACQUISITION_REASONS_ACCEPT = """
    {
      "is_error": 0,
      "message": "success",
      "data": {
        "type": "accept",
        "reasons": [
          {"id": 3, "reason": "লেনদেন নিয়মিত করে"},
          {"id": 4, "reason": "ব্যবসা ভালো চলছে"},
          {"id": 5, "reason": "দোকান সবসময় খোলা থাকে"},
          {"id": 6, "reason": "আগের ঋণ ঠিকমতো শোধ করেছে"},
          {"id": 7, "reason": "পেমেন্ট নিতে আগ্রহী"}
        ]
      }
    }
    """.trimIndent()

    val ACQUISITION_REASONS_REJECT = """
    {
      "is_error": 0,
      "message": "success",
      "data": {
        "type": "reject",
        "reasons": [
          {"id": 8, "reason": "প্রয়োজনীয় কাগজপত্র অসম্পূর্ণ"},
          {"id": 9, "reason": "ঠিকানা যাচাই করা যায়নি"},
          {"id": 10, "reason": "প্রিমিয়ামনেস স্কোর প্রত্যাশিত মানের কম"},
          {"id": 11, "reason": "যোগাযোগের তথ্যে অসামঞ্জস্য"},
          {"id": 12, "reason": "একই ওয়ালেট নম্বর ইতিমধ্যে নিবন্ধিত"}
        ]
      }
    }
    """.trimIndent()

    val ACQUISITION_DECISION_SUCCESS = """
    {
      "is_error": false,
      "message": "success"
    }
    """.trimIndent()

    // Same "questions" shape as :formbuilder's SPECIFIC_JSON_FORM preview fixture (parsed via
    // parseSpecificFormSchema, not parseFormSchema) - kept as a verbatim copy here since :network
    // can't depend on :formbuilder to reuse that constant directly.
    val SPECIFIC_FORM = """
    {
      "questions": [
        {
          "key": "q1",
          "question": "দোকানটি কি মার্কেটের ভিতরে অবস্থিত?",
          "type": "single_choice",
          "orientation": "horizontal",
          "is_dependent": false,
          "answers": [
            {"key": "yes", "value": "হ্যাঁ"},
            {"key": "no", "value": "না"}
          ]
        },
        {
          "key": "q2",
          "question": "মার্কেট বা দোকানটি কি শীতাতপ নিয়ন্ত্রিত?",
          "type": "single_choice",
          "orientation": "horizontal",
          "is_dependent": false,
          "answers": [
            {"key": "yes", "value": "হ্যাঁ"},
            {"key": "no", "value": "না"}
          ]
        },
        {
          "key": "q3",
          "question": "দোকানটির প্রবেশ পথে দরজা কি ধরণের?",
          "type": "single_choice",
          "orientation": "horizontal",
          "is_dependent": false,
          "answers": [
            {"key": "glass", "value": "গ্লাস"},
            {"key": "wood", "value": "কাঠ"},
            {"key": "thai_glass", "value": "থাই গ্লাস"},
            {"key": "none", "value": "কোনো দরজা নেই"}
          ]
        },
        {
          "key": "q4",
          "question": "দোকানের বাহিরে কি কোন আলোকিত সাইন বোর্ড আছে?",
          "type": "single_choice",
          "orientation": "horizontal",
          "is_dependent": false,
          "answers": [
            {"key": "yes", "value": "হ্যাঁ", "show": ["q5"]},
            {"key": "no", "value": "না", "hide": ["q5"]}
          ]
        },
        {
          "key": "q5",
          "question": "দোকানটির আলোকিত সাইন বোর্ড কি পরিষ্কার পরিছন্ন?",
          "type": "single_choice",
          "orientation": "horizontal",
          "is_dependent": true,
          "depends_on": {"question": "q4", "answer": "yes"},
          "answers": [
            {"key": "yes", "value": "হ্যাঁ"},
            {"key": "no", "value": "না"}
          ]
        },
        {
          "key": "q6",
          "question": "দোকানটির ভেতরে কয়টি টিউব লাইট আছে?",
          "type": "single_choice",
          "orientation": "vertical",
          "is_dependent": false,
          "answers": [
            {"key": "none", "value": "কোনো টিউবলাইট নেই"},
            {"key": "1_to_3", "value": "১ থেকে ৩"},
            {"key": "4_to_6", "value": "৪ থেকে ৬"},
            {"key": "7_to_10", "value": "৭ থেকে ১০"},
            {"key": "more_than_10", "value": "১০ এর অধিক"}
          ]
        },
        {
          "key": "q7",
          "question": "দোকানটির ভেতরে কয়টি স্পট লাইট আছে?",
          "type": "single_choice",
          "orientation": "vertical",
          "is_dependent": false,
          "answers": [
            {"key": "none", "value": "কোনো স্পটলাইট নেই"},
            {"key": "1_to_5", "value": "১ থেকে ৫"},
            {"key": "6_to_10", "value": "৬ থেকে ১০"},
            {"key": "11_to_15", "value": "১১ থেকে ১৫"},
            {"key": "more_than_15", "value": "১৫ এর অধিক"}
          ]
        },
        {
          "key": "q8",
          "question": "দোকানে আলাদা ক্যাশ কাউন্টার আছে?",
          "type": "single_choice",
          "orientation": "horizontal",
          "is_dependent": false,
          "answers": [
            {"key": "yes", "value": "হ্যাঁ"},
            {"key": "no", "value": "না"}
          ]
        },
        {
          "key": "q9",
          "question": "দোকানটি কি কার্ডের মাধ্যমে পেমেন্ট গ্রহণ করে?",
          "type": "single_choice",
          "orientation": "horizontal",
          "is_dependent": false,
          "answers": [
            {"key": "yes", "value": "হ্যাঁ"},
            {"key": "no", "value": "না"}
          ]
        },
        {
          "key": "q10",
          "question": "দোকানটি কি প্রিন্টেড বিল প্রদান করে?",
          "type": "single_choice",
          "orientation": "horizontal",
          "is_dependent": false,
          "answers": [
            {"key": "yes", "value": "হ্যাঁ"},
            {"key": "no", "value": "না"}
          ]
        }
      ]
    }
    """.trimIndent()
}
