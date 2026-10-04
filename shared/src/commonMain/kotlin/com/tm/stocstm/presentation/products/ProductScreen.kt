package com.tm.stocstm.presentation.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tm.stocstm.domain.model.ProductStock
import com.tm.stocstm.presentation.ui.background
import com.tm.stocstm.presentation.ui.neutral
import com.tm.stocstm.presentation.ui.primary
import com.tm.stocstm.presentation.ui.secondary
import com.tm.stocstm.presentation.ui.tertiary
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import stocstm.shared.generated.resources.Res
import stocstm.shared.generated.resources.*

@Composable
fun ProductScreen(
    viewModel: ProductViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopHeaderSection() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.add() },
                containerColor = primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(AddIcon, contentDescription = stringResource(Res.string.add_product))
            }
        },
        containerColor = background
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primary)
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.error}",
                        color = tertiary
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title & Auto-sorted row
                    item {
                        ShoppingListTitleHeader()
                    }

                    // Status counter chips row
                    item {
                        SummaryStatusChipsRow(state.products)
                    }

                    // Filter tabs (All Items, Need to buy, Stocked)
                    item {
                        FilterTabsSection(
                            selectedTab = state.filterState,
                            onTabSelected = {
                                viewModel.onEvent(ProductsEvent.ProductFiltersChange(
                                    FilterState.entries[it]
                                ))
                            }
                        )
                    }

                    if (state.filterState == FilterState.ALL || state.filterState == FilterState.FOR_BUY) {
                        // Section 1: Need to buy now
                        item {
                            SectionHeader(
                                color = tertiary,
                                title = stringResource(Res.string.section_need_to_buy),
                                countText = stringResource(
                                    Res.string.items_count,
                                    state.products.size
                                )
                            )
                        }
                        items(state.products) { product ->
                            if (product.product.type == ProductStock.TO_BUY) {
                                NeedToBuyItemCard(
                                    product = product,
                                    onProductSelected = {
                                        viewModel.onEvent(ProductsEvent.SelectProduct(product.product.id))
                                    }
                                )
                            }
                        }
                    }

                    if (state.filterState == FilterState.ALL || state.filterState == FilterState.IN_STOCK) {
                        // Section 2: Running low soon
                        item {
                            SectionHeader(
                                color = secondary,
                                title = stringResource(Res.string.section_running_low),
                                subtitle = stringResource(Res.string.section_running_low_sub),
                                countText = stringResource(Res.string.zero_items)
                            )
                        }
                        items(state.products) { product ->
                            if (product.product.type == ProductStock.LOW_STOCK) {
                                NeedToBuyItemCard(
                                    product = product,
                                    onProductSelected = {
                                        viewModel.onEvent(ProductsEvent.SelectProduct(product.product.id))
                                    }
                                )
                            }
                        }

                        // Section 3: Stocked at home
                        item {
                            SectionHeader(
                                color = primary,
                                title = stringResource(Res.string.section_stocked),
                                countText = stringResource(Res.string.zero_items),
                                isExpandable = true
                            )
                        }
                        items(state.products) { product ->
                            if (product.product.type == ProductStock.IN_STOCK) {
                                NeedToBuyItemCard(
                                    product = product,
                                    onProductSelected = {
                                        viewModel.onEvent(ProductsEvent.SelectProduct(product.product.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopHeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = primary,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = BagIcon,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(Res.string.app_name),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = neutral
                )
                Text(
                    text = stringResource(Res.string.app_subtitle),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ShoppingListTitleHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.shopping_list_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = neutral
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tertiary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = stringResource(Res.string.to_buy_count),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tertiary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryStatusChipsRow(products: List<ProductSelection>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val needToBuyCount = products.count { it.product.type == ProductStock.TO_BUY }
        StatusCounterChip(
            color = tertiary,
            text = "$needToBuyCount ${stringResource(Res.string.chip_need_to_buy)}",
            modifier = Modifier.weight(1f)
        )

        val lowStockCount = products.count { it.product.type == ProductStock.LOW_STOCK }
        StatusCounterChip(
            color = secondary,
            text = "$lowStockCount ${stringResource(Res.string.chip_running_low)}",
            modifier = Modifier.weight(1f)
        )

        val inStockCount = products.count { it.product.type == ProductStock.IN_STOCK }
        StatusCounterChip(
            color = primary,
            text = "$inStockCount ${stringResource(Res.string.chip_stocked)}",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatusCounterChip(
    color: Color,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
private fun FilterTabsSection(
    selectedTab: FilterState,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        stringResource(Res.string.tab_all),
        stringResource(Res.string.tab_need_to_buy),
        stringResource(Res.string.tab_stocked)
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedTab.ordinal == index
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) primary else Color.White,
                modifier = Modifier.clickable { onTabSelected(index) }
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    color: Color,
    title: String,
    subtitle: String? = null,
    countText: String,
    isExpandable: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = neutral
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = countText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            if (isExpandable) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = ChevronDownIcon,
                    contentDescription = stringResource(Res.string.expand),
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun NeedToBuyItemCard(
    product: ProductSelection,
    onProductSelected: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.clickable { onProductSelected() }.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = product.isSelected,
                onCheckedChange = { onProductSelected() },
                colors = CheckboxDefaults.colors(checkedColor = primary)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = neutral
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tertiary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = stringResource(Res.string.status_ran_out),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = tertiary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = product.product.quantity,
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = stringResource(Res.string.category_food),
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.lasts_days, product.product.durationDays),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = {}) {
                Icon(
                    MoreVertIcon,
                    contentDescription = stringResource(Res.string.options),
                    tint = Color.Gray
                )
            }
        }
    }
}

// Vector Icons
private val AddIcon: ImageVector
    get() = ImageVector.Builder("Add", 24.dp, 24.dp, 24f, 24f)
        .path(fill = SolidColor(Color.White)) {
            moveTo(19f, 13f); lineTo(13f, 13f); lineTo(13f, 19f); lineTo(11f, 19f); lineTo(
            11f,
            13f
        ); lineTo(5f, 13f); lineTo(5f, 11f); lineTo(11f, 11f); lineTo(11f, 5f); lineTo(
            13f,
            5f
        ); lineTo(13f, 11f); lineTo(19f, 11f); close()
        }.build()

private val BagIcon: ImageVector
    get() = ImageVector.Builder("Bag", 24.dp, 24.dp, 24f, 24f)
        .path(fill = SolidColor(Color.White)) {
            moveTo(18f, 6f); lineTo(16f, 6f); lineTo(16f, 5f); curveTo(
            16f,
            2.79f,
            14.21f,
            1f,
            12f,
            1f
        ); curveTo(9.79f, 1f, 8f, 2.79f, 8f, 5f); lineTo(8f, 6f); lineTo(6f, 6f); curveTo(
            4.9f,
            6f,
            4f,
            6.9f,
            4f,
            8f
        ); lineTo(4f, 20f); curveTo(4f, 21.1f, 4.9f, 22f, 6f, 22f); lineTo(18f, 22f); curveTo(
            19.1f,
            22f,
            20f,
            21.1f,
            20f,
            20f
        ); lineTo(20f, 8f); curveTo(20f, 6.9f, 19.1f, 6f, 18f, 6f); close(); moveTo(
            10f,
            5f
        ); curveTo(10f, 3.9f, 10.9f, 3f, 12f, 3f); curveTo(13.1f, 3f, 14f, 3.9f, 14f, 5f); lineTo(
            14f,
            6f
        ); lineTo(10f, 6f); close()
        }.build()

private val BellIcon: ImageVector
    get() = ImageVector.Builder("Bell", 24.dp, 24.dp, 24f, 24f)
        .path(fill = SolidColor(Color.White)) {
            moveTo(12f, 22f); curveTo(13.1f, 22f, 14f, 21.1f, 14f, 20f); lineTo(10f, 20f); curveTo(
            10f,
            21.1f,
            10.89f,
            22f,
            12f,
            22f
        ); close(); moveTo(18f, 16f); lineTo(18f, 11f); curveTo(
            18f,
            7.93f,
            16.36f,
            5.36f,
            13.5f,
            4.68f
        ); lineTo(13.5f, 4f); curveTo(13.5f, 3.17f, 12.83f, 2.5f, 12f, 2.5f); curveTo(
            11.17f,
            2.5f,
            10.5f,
            3.17f,
            10.5f,
            4f
        ); lineTo(10.5f, 4.68f); curveTo(7.63f, 5.36f, 6f, 7.92f, 6f, 11f); lineTo(6f, 16f); lineTo(
            4f,
            18f
        ); lineTo(4f, 19f); lineTo(20f, 19f); lineTo(20f, 18f); close()
        }.build()

private val PersonIcon: ImageVector
    get() = ImageVector.Builder("Person", 24.dp, 24.dp, 24f, 24f)
        .path(fill = SolidColor(Color.White)) {
            moveTo(12f, 12f); curveTo(14.21f, 12f, 16f, 10.21f, 16f, 8f); curveTo(
            16f,
            5.79f,
            14.21f,
            4f,
            12f,
            4f
        ); curveTo(9.79f, 4f, 8f, 5.79f, 8f, 8f); curveTo(
            8f,
            10.21f,
            9.79f,
            12f,
            12f,
            12f
        ); close(); moveTo(12f, 14f); curveTo(9.33f, 14f, 4f, 15.34f, 4f, 18f); lineTo(
            4f,
            20f
        ); lineTo(20f, 20f); lineTo(20f, 18f); curveTo(20f, 15.34f, 14.67f, 14f, 12f, 14f); close()
        }.build()

private val SparkleIcon: ImageVector
    get() = ImageVector.Builder("Sparkle", 24.dp, 24.dp, 24f, 24f)
        .path(fill = SolidColor(Color.White)) {
            moveTo(12f, 2f); lineTo(14.5f, 9.5f); lineTo(22f, 12f); lineTo(
            14.5f,
            14.5f
        ); lineTo(12f, 22f); lineTo(9.5f, 14.5f); lineTo(2f, 12f); lineTo(9.5f, 9.5f); close()
        }.build()

private val ChevronDownIcon: ImageVector
    get() = ImageVector.Builder("ChevronDown", 24.dp, 24.dp, 24f, 24f)
        .path(fill = SolidColor(Color.White)) {
            moveTo(7.41f, 8.59f); lineTo(12f, 13.17f); lineTo(16.59f, 8.59f); lineTo(
            18f,
            10f
        ); lineTo(12f, 16f); lineTo(6f, 10f); close()
        }.build()

private val RadioCircleIcon: ImageVector
    get() = ImageVector.Builder("RadioCircle", 24.dp, 24.dp, 24f, 24f)
        .path(fill = SolidColor(Color.White)) {
            moveTo(12f, 2f); curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f); curveTo(
            2f,
            17.52f,
            6.48f,
            22f,
            12f,
            22f
        ); curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f); curveTo(
            22f,
            6.48f,
            17.52f,
            2f,
            12f,
            2f
        ); close(); moveTo(12f, 20f); curveTo(7.59f, 20f, 4f, 16.41f, 4f, 12f); curveTo(
            4f,
            7.59f,
            7.59f,
            4f,
            12f,
            4f
        ); curveTo(16.41f, 4f, 20f, 7.59f, 20f, 12f); curveTo(
            20f,
            16.41f,
            16.41f,
            20f,
            12f,
            20f
        ); close()
        }.build()

private val MoreVertIcon: ImageVector
    get() = ImageVector.Builder("MoreVert", 24.dp, 24.dp, 24f, 24f)
        .path(fill = SolidColor(Color.White)) {
            moveTo(12f, 8f); curveTo(13.1f, 8f, 14f, 7.1f, 14f, 6f); curveTo(
            14f,
            4.9f,
            13.1f,
            4f,
            12f,
            4f
        ); curveTo(10.9f, 4f, 10f, 4.9f, 10f, 6f); curveTo(
            10f,
            7.1f,
            10.9f,
            8f,
            12f,
            8f
        ); close(); moveTo(12f, 10f); curveTo(10.9f, 10f, 10f, 10.9f, 10f, 12f); curveTo(
            10f,
            13.1f,
            10.9f,
            14f,
            12f,
            14f
        ); curveTo(13.1f, 14f, 14f, 13.1f, 14f, 12f); curveTo(
            14f,
            10.9f,
            13.1f,
            10f,
            12f,
            10f
        ); close(); moveTo(12f, 16f); curveTo(10.9f, 16f, 10f, 16.9f, 10f, 18f); curveTo(
            10f,
            19.1f,
            10.9f,
            20f,
            12f,
            20f
        ); curveTo(13.1f, 20f, 14f, 19.1f, 14f, 18f); curveTo(
            14f,
            16.9f,
            13.1f,
            16f,
            12f,
            16f
        ); close()
        }.build()
