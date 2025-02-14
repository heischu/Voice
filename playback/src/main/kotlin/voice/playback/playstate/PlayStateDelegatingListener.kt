package voice.playback.playstate

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import voice.playback.PlayerController
import javax.inject.Inject

class PlayStateDelegatingListener
@Inject constructor(
  private val playStateManager: PlayStateManager,
  private val playerController: PlayerController,
) : Player.Listener {

  private lateinit var player: Player

  fun attachTo(player: Player) {
    this.player = player
    player.addListener(this)
    updatePlayState()
  }

  override fun onPlaybackStateChanged(playbackState: Int) {
    updatePlayState()
  }

  override fun onPlayWhenReadyChanged(
    playWhenReady: Boolean,
    reason: Int,
  ) {
    updatePlayState()
  }

  override fun onMediaItemTransition(
    mediaItem: MediaItem?,
    reason: Int,
  ) {
    if (playStateManager.sleepAtEoc) {
      playStateManager.sleepAtEoc = false
      playerController.pauseAtStart()
    }
  }

  private fun updatePlayState() {
    val playbackState = player.playbackState
    playStateManager.playState = when {
      playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE -> PlayStateManager.PlayState.Paused
      player.playWhenReady -> PlayStateManager.PlayState.Playing
      else -> PlayStateManager.PlayState.Paused
    }
  }
}
