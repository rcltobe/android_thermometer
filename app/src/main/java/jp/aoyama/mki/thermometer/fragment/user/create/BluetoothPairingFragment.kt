package jp.aoyama.mki.thermometer.fragment.user.create

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import jp.aoyama.mki.thermometer.databinding.BluetoothPairingFragmentBinding
import jp.aoyama.mki.thermometer.fragment.bluetooth.BluetoothListAdapter
import jp.aoyama.mki.thermometer.fragment.bluetooth.BluetoothViewHolder
import jp.aoyama.mki.thermometer.util.BluetoothScanController
import jp.aoyama.mki.thermometer.viewmodels.CreateUserSharedViewModel

class BluetoothPairingFragment : Fragment(), BluetoothViewHolder.CallbackListener {

    private lateinit var mBinding: BluetoothPairingFragmentBinding
    private val mAdapter: BluetoothListAdapter = BluetoothListAdapter(this)
    private val mViewModel: CreateUserSharedViewModel by viewModels({ requireActivity() })
    private val mBluetoothScanController: BluetoothScanController by lazy {
        BluetoothScanController(requireContext())
    }


    private val mRequestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) findBluetoothDevices()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = BluetoothPairingFragmentBinding.inflate(inflater, container, false)

        mBinding.apply {
            buttonSkip.setOnClickListener {
                findNavController().navigate(BluetoothPairingFragmentDirections.pairingToConfirm())
            }
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }

            listBluetoothDevices.adapter = mAdapter
            listBluetoothDevices.layoutManager = LinearLayoutManager(requireContext())
        }

        mBluetoothScanController.devicesLiveData.observe(viewLifecycleOwner) { devices ->
            mBinding.progressCircular.visibility =
                if (devices.isEmpty()) View.VISIBLE
                else View.GONE

            mAdapter.submitList(devices.map { it.device })
        }

        val accessFineLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) findBluetoothDevices()
        else mRequestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mBluetoothScanController.cancelDiscovery()
    }

    private fun findBluetoothDevices() {
        mBluetoothScanController.startDiscovery()
    }

    // =====================================
    // BluetoothViewHolder.CallbackListener
    // =====================================
    override fun onClick(device: BluetoothDevice) {
        mViewModel.bluetoothDeviceName = device.name
        mViewModel.bluetoothMacAddress = device.address
        findNavController().navigate(BluetoothPairingFragmentDirections.pairingToConfirm())
    }
}