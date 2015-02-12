import com.temboo.Library.Twitter.OAuth.FinalizeOAuth;
import com.temboo.Library.Twitter.OAuth.InitializeOAuth;
import com.temboo.Library.Twitter.Timelines.UserTimeline;
import com.temboo.core.TembooException;
import com.temboo.core.*;
import com.google.gson.*;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URI;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.lang.Math;


public class SMOG {

    public static double SMOGlevel( double polysyllable_count, double sentence_count ){

        if( (30/sentence_count) < 1 ){
            sentence_count = 30;
        }
        return (1.0430 * Math.sqrt(polysyllable_count * (30 / sentence_count)) + 3.1291);
    }

    public static void main( String[] args ) throws Exception,TembooException{

        // Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
        TembooSession session = new TembooSession("jts343", "myFirstApp", "185eae9d4f1d4dacacb8ca130559eed9");

        InitializeOAuth initializeOAuthChoreo = new InitializeOAuth(session);

// Get an InputSet object for the choreo
        InitializeOAuth.InitializeOAuthInputSet initializeOAuthInputs = initializeOAuthChoreo.newInputSet();

// Set inputs
        initializeOAuthInputs.set_ConsumerSecret("a6mN0m39cwfq1BT0Hf7XaSQpH4zyDKhoPraGGTgVy4Z4tFabpx");
        initializeOAuthInputs.set_ConsumerKey("il3tjyM0wtMR3DvkvsPDXBzEp");

// Execute Choreo
        InitializeOAuth.InitializeOAuthResultSet initializeOAuthResults = initializeOAuthChoreo.execute(initializeOAuthInputs);

// Enter name of screenname to use
        System.out.println("Enter the screenname of the person you with to use: ");
        String screenname = new BufferedReader( new InputStreamReader(System.in)).readLine().trim();

// Open Authorization in default browser
        Desktop d = Desktop.getDesktop();
        d.browse(new URI( initializeOAuthResults.get_AuthorizationURL() ));

//        System.out.println("Go Here: " + initializeOAuthResults.get_AuthorizationURL());
        System.out.println("Press ENTER after accepting...");

        // Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
// TembooSession session = new TembooSession("jts343", "myFirstApp", "185eae9d4f1d4dacacb8ca130559eed9");

        FinalizeOAuth finalizeOAuthChoreo = new FinalizeOAuth(session);

// Get an InputSet object for the choreo
        FinalizeOAuth.FinalizeOAuthInputSet finalizeOAuthInputs = finalizeOAuthChoreo.newInputSet();

// Set inputs
        finalizeOAuthInputs.set_CallbackID(initializeOAuthResults.get_CallbackID());
        finalizeOAuthInputs.set_OAuthTokenSecret(initializeOAuthResults.get_OAuthTokenSecret());
        finalizeOAuthInputs.set_ConsumerSecret("a6mN0m39cwfq1BT0Hf7XaSQpH4zyDKhoPraGGTgVy4Z4tFabpx");
        finalizeOAuthInputs.set_ConsumerKey("il3tjyM0wtMR3DvkvsPDXBzEp");

// Execute Choreo
        FinalizeOAuth.FinalizeOAuthResultSet finalizeOAuthResults = finalizeOAuthChoreo.execute(finalizeOAuthInputs);

// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
// TembooSession session = new TembooSession("jts343", "myFirstApp", "185eae9d4f1d4dacacb8ca130559eed9");

        UserTimeline userTimelineChoreo = new UserTimeline(session);

// Get an InputSet object for the choreo
        UserTimeline.UserTimelineInputSet userTimelineInputs = userTimelineChoreo.newInputSet();

// Set inputs
        userTimelineInputs.set_ExcludeReplies("true");
        userTimelineInputs.set_ScreenName(screenname);
        userTimelineInputs.set_Count("30");
        userTimelineInputs.set_AccessToken("994225412-6q9vUKy1H8lKSVEJxGDUfASChn5KBHtAJAedaJs7");
        userTimelineInputs.set_IncludeRetweets("true");
        userTimelineInputs.set_AccessTokenSecret("glX7ylfzvCJ88ZD6u6Nq0lfYgLidlS39ZbLksqNKNKTsA");
        userTimelineInputs.set_ConsumerSecret("a6mN0m39cwfq1BT0Hf7XaSQpH4zyDKhoPraGGTgVy4Z4tFabpx");
        userTimelineInputs.set_ConsumerKey("il3tjyM0wtMR3DvkvsPDXBzEp");

// Execute Choreo
        UserTimeline.UserTimelineResultSet userTimelineResults = userTimelineChoreo.execute(userTimelineInputs);
        String[] source = userTimelineResults.get_Response().split("[*]");

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(userTimelineResults.get_Response());

//        System.out.println( root );

        String[] tweetArray = new String[100];

        for( int i = 0; i < (root.getAsJsonArray().size() - 1); i++ ){
//            if( root.getAsJsonArray().size() < 30 ) {
//                System.out.println( "Too few tweets returned. Unable to compute SMOG level for this user" );
//                System.exit(2);
//            }
            tweetArray[i] = root.getAsJsonArray()
                    .get(i).getAsJsonObject()
                    .get("text").getAsString();
//            tweetArray[i] = tweetArray[i].split(" ");
//            System.out.println(tweetArray[i]);
        }

        String sURL;
        URL reqURL;
        int polysyllable_count = 0;

        for( int i = 0; i < tweetArray.length; i++){
            if( tweetArray[i+1] == null) {
                break;
            }

            System.out.println();
            for( int j = 0; j < (tweetArray[i].split(" ").length - 1); j++ ) {
                if( tweetArray[i].split(" ")[j+1] == null) {
                    break;
                }
                tweetArray[i].split(" ");
                tweetArray[i].replace("/","");
//                System.out.println("\n" + tweetArray[0].split(" ")[j]);

                System.out.println("Feeding word: " + tweetArray[i].split(" ")[j]
                                .replace("/","")
                                .replace(":","")
                                .replace(".","")
                                .replace("\"","")
                                .replace("—","-")
                                .replace("#","")
                                .replace("’","'")
                                .replace("%",""));

                // Remove special characters that people use to make my life difficult
                sURL = "http://api.wordnik.com/v4/word.json/" +
                        tweetArray[i].split(" ")[j]
                                .replace("/","")
                                .replace(":","")
                                .replace(".","")
                                .replace("\"","")
                                .replace("—","-")
                                .replace("#","")
                                .replace("’","'")
                                .replace("%","") +
                        "/hyphenation?useCanonical=false&limit=50&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";

                reqURL = new URL(sURL);
                HttpURLConnection req = (HttpURLConnection) reqURL.openConnection();
                req.connect();

                JsonElement wordnikRoot = jp.parse(new InputStreamReader((InputStream)
                        req.getContent()));
                JsonArray wordnikRootObj = root.getAsJsonArray();

                if (wordnikRootObj.size() >= 2) {
                    polysyllable_count += wordnikRootObj.size();

                    }

                }
            }
        System.out.println( polysyllable_count );

        double grade = SMOGlevel( polysyllable_count, tweetArray.length);
        System.out.println();
        System.out.println( "Grade: " + grade );
    }
}
