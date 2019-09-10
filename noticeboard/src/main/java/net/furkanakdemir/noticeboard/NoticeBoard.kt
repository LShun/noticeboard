package net.furkanakdemir.noticeboard

import android.content.Context
import androidx.fragment.app.FragmentActivity
import net.furkanakdemir.noticeboard.config.ConfigRepository
import net.furkanakdemir.noticeboard.data.repository.NoticeBoardRepository
import net.furkanakdemir.noticeboard.di.DaggerInjector
import net.furkanakdemir.noticeboard.ui.NoticeBoardActivity
import net.furkanakdemir.noticeboard.ui.NoticeBoardDialogFragment
import net.furkanakdemir.noticeboard.util.color.ColorProvider
import javax.inject.Inject

class NoticeBoard(
    val context: Context
) {

    private var sourceType: Source = Source.Dynamic()
    private var displayOptions: DisplayOptions = DisplayOptions.ACTIVITY
    private var title: String = TITLE_DEFAULT

    init {
        DaggerInjector.buildComponent(context)
        DaggerInjector.component?.inject(this)
    }

    @Inject
    lateinit var noticeBoardRepository: NoticeBoardRepository

    @Inject
    lateinit var configRepository: ConfigRepository

    fun pin(func: NoticeBoard.() -> Unit): NoticeBoard {
        this.func()
        this.pin()
        return this
    }

    fun source(source: Source) {
        this.sourceType = source
    }

    fun displayIn(displayOptions: DisplayOptions) {
        this.displayOptions = displayOptions
    }

    fun title(text: String) {
        title = if (text.isEmpty()) {
            TITLE_DEFAULT
        } else {
            text
        }
    }

    fun colorProvider(colorProvider: ColorProvider) {
        configRepository.saveColorProvider(colorProvider)
    }

    private fun pin() {
        noticeBoardRepository.fetchChanges(sourceType)

        when (displayOptions) {
            DisplayOptions.ACTIVITY -> context.startActivity(
                NoticeBoardActivity.createIntent(context, title)
            )
            DisplayOptions.DIALOG -> {
                val fm = (context as FragmentActivity).supportFragmentManager
                val noticeBoardDialogFragment = NoticeBoardDialogFragment.newInstance(title)
                noticeBoardDialogFragment.show(
                    fm,
                    NoticeBoardDialogFragment::class.java.canonicalName
                )
            }
        }
    }

    companion object {
        const val TITLE_DEFAULT = "NoticeBoard"
        const val KEY_TITLE = "KEY_TITLE"
    }
}