/// Search filters for both the SearchExtractor
class YoutubeSearchFilter {

  // Search filters
  static const String all = "all";
  static const String videos = "videos";
  static const String channels = "channels";
  static const String playlists = "playlists";

  // Music search filters
  static const String musicSongs = "music_songs";
  static const String musicVideos = "music_videos";
  static const String musicAlbums = "music_albums";
  static const String musicPlaylists = "music_playlists";
  static const String musicArtists = "music_artists";

  // Lists
  static final List<String> searchFilters = [
    videos, channels, playlists, musicSongs, musicVideos
  ];

}