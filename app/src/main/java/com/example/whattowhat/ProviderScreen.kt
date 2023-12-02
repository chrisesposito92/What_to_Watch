package com.example.whattowhat

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whattowhat.model.Provider
import com.example.whattowhat.model.ProviderData




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderSelectionScreen(navController: NavController, drawerState: DrawerState) {
    val context = LocalContext.current
    val providers = ProviderData.providers
    val sortedProviders = providers.sortedBy { it.display_priority }.filter { it.provider_id != 0 }
    val selectedProviderIds =  RememberProviders().getSelectedProviders(context).map { it }
    val selectedProvidersOld = sortedProviders.filter { it.provider_id.toString() in selectedProviderIds }
    val selectedProviders = remember { mutableStateListOf(*selectedProvidersOld.toTypedArray()) }
    var selectAll by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Provider Selection", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                enabled = !drawerState.isOpen,
                onClick = {
                selectAll = !selectAll
                if (selectAll) {
                    selectedProviders.clear()
                    selectedProviders.addAll(sortedProviders)
                } else {
                    selectedProviders.clear()
                }
            }) {
                Text(if (selectAll) "Deselect All" else "Select All")
            }

            Button(onClick = {
                RememberProviders().saveSelectedProviders(context, selectedProviders.map { it.provider_id.toString() }.toSet())
                Toast.makeText(context, "Providers Saved", Toast.LENGTH_SHORT).show()
            }) {
                Text("Save")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(sortedProviders.size) { index ->
                val provider = sortedProviders[index]
                ProviderListItem(provider, selectedProviders)
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProviderListItem(provider: Provider, selectedProviders: SnapshotStateList<Provider>) {
    val isSelected = derivedStateOf { provider in selectedProviders }
    Row(Modifier.clickable {
        if (isSelected.value) {
            selectedProviders.remove(provider)
        } else {
            selectedProviders.add(provider)
        }
    }) {
        Switch(checked = isSelected.value, onCheckedChange = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(modifier = Modifier.padding(top = 6.dp), text = provider.provider_name)
    }
}