import 'package:social_flow_data_extracter/newpipeextractor_dart.dart';
import 'package:social_flow_data_extracter/utils/re_captcha.dart';
import 'package:social_flow_data_extracter/utils/streams_parser.dart';

class TrendingExtractor {
  /// Returns a list of StreamInfoItem containing Trending Videos
  static Future<List<StreamInfoItem>> getTrendingVideos() async {
    Future<dynamic> task() => NewPipeExtractorDart.extractorChannel
        .invokeMethod("getTrendingStreams");
    var info = await task();
    // Check if we got reCaptcha needed response
    info = await ReCaptchaPage.checkInfo(info, task);
    return StreamsParser.parseStreamListFromMap(info);
  }
}
