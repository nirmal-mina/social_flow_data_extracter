import 'package:flutter/services.dart';

// Models
export 'models/channel.dart';
export 'models/comment.dart';
export 'models/filters.dart';
export 'models/playlist.dart';
export 'models/search.dart';
export 'models/video.dart';
export 'models/streamSegment.dart';

// InfoItems
export 'models/infoItems/channel.dart';
export 'models/infoItems/playlist.dart';
export 'models/infoItems/video.dart';

// Streams
export 'models/streams/audioOnlyStream.dart';
export 'models/streams/videoOnlyStream.dart';
export 'models/streams/videoStream.dart';

class NewPipeExtractorDart {
  static const MethodChannel extractorChannel =
      MethodChannel('newpipeextractor_dart');

  // --- Channel helper methods ---
  static Future<Map<dynamic, dynamic>> getChannel(String channelUrl) async {
    final res = await extractorChannel
        .invokeMethod('getChannel', {'channelUrl': channelUrl});
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<List<dynamic>> getChannelUploads(String channelUrl) async {
    final res = await extractorChannel
        .invokeMethod('getChannelUploads', {'channelUrl': channelUrl});
    return List<dynamic>.from(res);
  }

  static Future<Map<dynamic, dynamic>> getChannelNextPage() async {
    final res = await extractorChannel.invokeMethod('getChannelNextPage');
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<List<dynamic>> getVideoInfoAndStreams(String videoUrl) async {
    final res = await extractorChannel
        .invokeMethod('getVideoInfoAndStreams', {'videoUrl': videoUrl});
    return List<dynamic>.from(res);
  }

  static Future<Map<dynamic, dynamic>> getVideoInformation(
      String videoUrl) async {
    final res = await extractorChannel
        .invokeMethod('getVideoInformation', {'videoUrl': videoUrl});
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<Map<dynamic, dynamic>> getAllVideoStreams(
      String videoUrl) async {
    final res = await extractorChannel
        .invokeMethod('getAllVideoStreams', {'videoUrl': videoUrl});
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<Map<dynamic, dynamic>> getVideoOnlyStreams(
      String videoUrl) async {
    final res = await extractorChannel
        .invokeMethod('getVideoOnlyStreams', {'videoUrl': videoUrl});
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<Map<dynamic, dynamic>> getAudioOnlyStreams(
      String videoUrl) async {
    final res = await extractorChannel
        .invokeMethod('getAudioOnlyStreams', {'videoUrl': videoUrl});
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<Map<dynamic, dynamic>> getVideoStreams(String videoUrl) async {
    final res = await extractorChannel
        .invokeMethod('getVideoStreams', {'videoUrl': videoUrl});
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<Map<dynamic, dynamic>> getVideoSegments(String videoUrl) async {
    final res = await extractorChannel
        .invokeMethod('getVideoSegments', {'videoUrl': videoUrl});
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<Map<dynamic, dynamic>> searchYoutube(
      String query, List<String>? filters) async {
    final res = await extractorChannel
        .invokeMethod('searchYoutube', {'query': query, 'filters': filters});
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<Map<dynamic, dynamic>> getNextPage() async {
    final res = await extractorChannel.invokeMethod('getNextPage');
    return Map<dynamic, dynamic>.from(res);
  }

  static Future<String?> getIdFromStreamUrl(String url) async {
    return await extractorChannel
        .invokeMethod('getIdFromStreamUrl', {'streamUrl': url});
  }

  static Future<String?> getIdFromPlaylistUrl(String url) async {
    return await extractorChannel
        .invokeMethod('getIdFromPlaylistUrl', {'playlistUrl': url});
  }

  static Future<String?> getIdFromChannelUrl(String url) async {
    return await extractorChannel
        .invokeMethod('getIdFromChannelUrl', {'channelUrl': url});
  }

  // Cookie related helpers
  static Future<void> setCookie(String cookie) async {
    await extractorChannel.invokeMethod('setCookie', {'cookie': cookie});
  }

  static Future<String?> getCookieByUrl(String url) async {
    return await extractorChannel.invokeMethod('getCookieByUrl', {'url': url});
  }

  static Future<String?> decodeCookie(String cookie) async {
    return await extractorChannel
        .invokeMethod('decodeCookie', {'cookie': cookie});
  }
}
