package com.demo.beavair.fragment.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tuyasdkdemo.R
import com.example.tuyasdkdemo.databinding.RowItemDeviceListBinding
import com.thingclips.smart.sdk.bean.DeviceBean

class DeviceListAdapter (private val onDeviceClicked: (DeviceBean) -> Unit) :
    ListAdapter<DeviceBean, DeviceListAdapter.DeviceViewHolder>(ComparatorDiffUtil()){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeviceListAdapter.DeviceViewHolder {
        val binding = RowItemDeviceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceListAdapter.DeviceViewHolder, position: Int) {
        val device = getItem(position)
        device?.let {
            holder.bind(it)
        }
    }

    inner class DeviceViewHolder(private val binding: RowItemDeviceListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: DeviceBean) {

            binding.txtDeviceName.text = bean.getName()

            if(bean.isOnline){
                binding.txtStatus.text = itemView.context.getString(R.string.device_online)
                binding.txtStatus.setTextColor(ContextCompat.getColor(itemView.context,R.color.green_txt))
            }else{
                binding.txtStatus.text = itemView.context.getString(R.string.device_offline)
                binding.txtStatus.setTextColor(ContextCompat.getColor(itemView.context,R.color.red_error_txt))
            }

            binding.root.setOnClickListener {
                onDeviceClicked(bean)
            }
        }
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<DeviceBean>() {
        override fun areItemsTheSame(oldItem: DeviceBean, newItem: DeviceBean): Boolean {
            return oldItem.devId == newItem.devId
        }

        override fun areContentsTheSame(oldItem: DeviceBean, newItem: DeviceBean): Boolean {
            return oldItem.devId == newItem.devId
        }

    }
}