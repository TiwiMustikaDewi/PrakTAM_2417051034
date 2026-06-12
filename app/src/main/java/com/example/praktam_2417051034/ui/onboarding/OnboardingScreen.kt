package com.example.praktam_2417051034.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051034.R
import kotlinx.coroutines.launch

data class OnboardingStep(
    val title: String,
    val description: String,
    val imageRes: Int,
    val tags: List<String> = emptyList()
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit = {}
) {
    val pages = listOf(
        OnboardingStep(
            title = "Take Control of Your Money",
            description = "Track your income and expenses effortlessly, stay organized, and always know where your money goes.",
            imageRes = R.drawable.onboardingpertama,
            tags = listOf("Expense Tracking", "Income Records", "Daily Budget")
        ),
        OnboardingStep(
            title = "Discover Smarter Financial Insights",
            description = "TIVO analyzes your spending habits and provides personalized recommendations to help you make better financial decisions.",
            imageRes = R.drawable.onboardingkedua,
            tags = listOf("TIVO Coach", "Smart Insights", "Spending Analysis")
        ),
        OnboardingStep(
            title = "Achieve Your Financial Goals",
            description = "Set savings targets, monitor your progress, and plan your future with confidence through financial simulations.",
            imageRes = R.drawable.onboardingketiga,
            tags = listOf("Savings Goals", "Future Simulation", "Financial Score")
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFF))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage > 0) {
                IconButton(onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF2563EB))
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
            TextButton(onClick = onFinish) {
                Text("Skip", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape)
                        .background(if (index == pagerState.currentPage) Color(0xFF2563EB) else Color(0xFFE5E7EB))
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { index ->
            val page = pages[index]
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(page.title, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827), lineHeight = 40.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(page.description, fontSize = 16.sp, color = Color(0xFF6B7280), lineHeight = 24.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Image(painterResource(page.imageRes), null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                }
                if (page.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        page.tags.forEach { tag ->
                            Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFEFF6FF), modifier = Modifier.padding(horizontal = 4.dp)) {
                                Text(tag, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Medium, maxLines = 1)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.height(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                repeat(pages.size) { i ->
                    Box(modifier = Modifier.padding(4.dp).clip(CircleShape).background(if (pagerState.currentPage == i) Color(0xFF2563EB) else Color(0xFFD1D5DB)).size(if (pagerState.currentPage == i) 10.dp else 8.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else { onFinish() }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (pagerState.currentPage < pages.size - 1) "Continue" else "Get Started", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (pagerState.currentPage == pages.size - 1) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", color = Color(0xFF6B7280), fontSize = 14.sp)
                TextButton(onClick = { /* Handle Login */ }, contentPadding = PaddingValues(0.dp)) {
                    Text("Login", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
