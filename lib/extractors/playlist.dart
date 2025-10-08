import 'dart:convert';

import 'package:social_flow_data_extracter/exceptions/bad_url_exception.dart';
import 'package:social_flow_data_extracter/newpipeextractor_dart.dart';
import 'package:social_flow_data_extracter/utils/re_captcha.dart';
import 'package:social_flow_data_extracter/utils/streams_parser.dart';
import 'package:social_flow_data_extracter/utils/string_checker.dart';

class PlaylistExtractor {
  /// Extract all the details of the given Playlist URL into a YoutubePlaylist object
  static Future<YoutubePlaylist> getPlaylistDetails(String? playlistUrl) async {
    if (playlistUrl == null || StringChecker.hasWhiteSpace(playlistUrl)) {
      throw const BadUrlException("Url is null or contains white space");
    }
    Future<dynamic> task() => NewPipeExtractorDart.extractorChannel
        .invokeMethod("getPlaylistDetails", {"playlistUrl": playlistUrl});
    var info = await task();
    // Check if we got reCaptcha needed response
    info = await ReCaptchaPage.checkInfo(info, task);
    return YoutubePlaylist(
        info['id'],
        info['name'],
        info['url'],
        info['uploaderName'],
        List<String>.from(jsonDecode(info['uploaderAvatars'])),
        info['uploaderUrl'],
        List<String>.from(jsonDecode(info['banners'])),
        List<String>.from(jsonDecode(info['thumbnails'])),
        int.parse(info['streamCount']));
  }

  /// Extract all the Streams from the given Playlist URL
  /// as a list of StreamInfoItem
  static Future<List<StreamInfoItem>> getPlaylistStreams(
      String? playlistUrl) async {
    if (playlistUrl == null || StringChecker.hasWhiteSpace(playlistUrl)) {
      throw const BadUrlException("Url is null or contains white space");
    }
    Future<dynamic> task() => NewPipeExtractorDart.extractorChannel
        .invokeMethod("getPlaylistStreams", {"playlistUrl": playlistUrl});
    var info = await task();
    // Check if we got reCaptcha needed response
    info = await ReCaptchaPage.checkInfo(info, task);
    return StreamsParser.parseStreamListFromMap(info);
  }
}
