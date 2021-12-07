package dev.mfazio.pennydrop.binding

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.Converters.convertColorToColorStateList
import dev.mfazio.pennydrop.R

@BindingAdapter("isHidden")
fun bindIsHidden(view: View, isInvisible: Boolean) {
    view.visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("playerSummaryAvatarSrc")
fun bindPlayerSummaryAvatarSrc(imageView: ImageView, isHuman: Boolean) {
    imageView.setImageResource(
        if (isHuman) {
            R.drawable.ic_baseline_face_24
        } else {
            R.drawable.ic_baseline_android_24
        }
    )
}

@BindingAdapter("playerSummaryAvatarTint")
fun bindPlayerSummaryAvatarTint(imageView: ImageView, isHuman: Boolean) {
    imageView.imageTintList = convertColorToColorStateList(
        imageView.context.getColor(
            if (isHuman) {
                android.R.color.holo_blue_bright
            } else {
                android.R.color.holo_green_light
            }
        )
    )
}

@BindingAdapter("slotLastRolled")
fun bindSlotLastRolled(view: View, lastRolled: Boolean) {
    view.isActivated = lastRolled
}