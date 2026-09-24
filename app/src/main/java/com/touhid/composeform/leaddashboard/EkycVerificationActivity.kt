package com.touhid.composeform.leaddashboard

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.touhid.composeform.ComposeFormAppTheme
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppButtonStyle
import com.touhid.composeform.designsystem.components.button.AppOutlinedButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing

// Stands in for a real eKYC verification flow (e.g. a third-party SDK's own Activity) - this
// codebase has no such SDK yet, so LeadDashboardScreen launches this via
// ActivityResultContracts.StartActivityForResult purely to exercise that wiring end to end:
// launch for a result -> user confirms/cancels -> RESULT_OK only then triggers the real
// submitEkyc API call back in LeadDashboardViewModel. No data is passed in or out - only the
// result code matters to the caller.
class EkycVerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormAppTheme {
                AppScaffold {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppSpacing.Medium),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium, Alignment.CenterVertically),
                    ) {
                        AppText(text = "ই-কেওয়াইসি ভেরিফিকেশন (ডামি)", style = AppTextStyle.TitleMedium)
                        AppText(
                            text = "প্রকৃত ভেরিফিকেশন ফ্লো এখানে বসবে - এই স্ক্রিনটি শুধু ফলাফল ফিরিয়ে দেওয়ার প্রবাহ পরীক্ষা করার জন্য।",
                            style = AppTextStyle.BodyMedium,
                        )
                        AppButton(
                            text = "Confirm",
                            buttonType = AppButtonStyle.Success,
                            onClick = {
                                setResult(Activity.RESULT_OK)
                                finish()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AppOutlinedButton(
                            text = "Cancel",
                            onClick = {
                                setResult(Activity.RESULT_CANCELED)
                                finish()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
