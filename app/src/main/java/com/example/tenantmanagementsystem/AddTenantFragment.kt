package com.example.tenantmanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tenantmanagementsystem.databinding.FragmentAddTenantBinding

class AddTenantFragment : Fragment() {

    private var _binding: FragmentAddTenantBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTenantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // saveButton ← android:id="@+id/saveButton" in fragment_add_tenant.xml
        binding.saveButton.setOnClickListener {
            // Read the three EditTexts in fragment_add_tenant.xml
            val name = binding.tenantNameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val rent = binding.rentEditText.text.toString().trim()

            // Lab 5 validation: mark every empty field as "Required"
            var valid = true
            if (name.isEmpty()) {
                binding.tenantNameEditText.error = "Required"
                valid = false
            }
            if (phone.isEmpty()) {
                binding.phoneEditText.error = "Required"
                valid = false
            }
            if (rent.isEmpty()) {
                binding.rentEditText.error = "Required"
                valid = false
            }
            if (!valid) {
                return@setOnClickListener
            }

            val tenant = Tenant(name, phone, rent)

            // Add to the shared list (the Dashboard and View Tenants read it)
            TenantRepository.addTenant(tenant)

            // SAFE ARGS: generated from the <action> in nav_graph.xml.
            // View Tenants has an <argument> of type Tenant, so this function
            // takes a Tenant and a mistake is a red error before you run.
            val action = AddTenantFragmentDirections
                .actionAddTenantFragmentToViewTenantsFragment(tenant)

            // Ask the NavController to follow the action
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
