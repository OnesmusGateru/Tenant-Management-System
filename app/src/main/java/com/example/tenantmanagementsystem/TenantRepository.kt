package com.example.tenantmanagementsystem

// "object" = a class with exactly ONE instance shared by the whole app,
// so every screen sees the same list of tenants.
object TenantRepository {

    val tenants = mutableListOf<Tenant>()

    fun addTenant(tenant: Tenant) {
        tenants.add(tenant)
    }

    // Converts each rent from text to a number (0.0 if it isn't a valid number)
    fun totalRent(): Double {
        return tenants.sumOf { it.rent.toDoubleOrNull() ?: 0.0 }
    }
}
