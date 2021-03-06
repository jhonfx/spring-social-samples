/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.showcase.twitter;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.twitter.TwitterApi;
import org.springframework.social.twitter.TwitterProfile;
import org.springframework.social.twitter.connect.TwitterServiceProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TwitterShowcaseController {

	private final TwitterServiceProvider twitterProvider;

	@Inject
	public TwitterShowcaseController(TwitterServiceProvider twitterProvider) {
		this.twitterProvider = twitterProvider;
	}

	@RequestMapping(value="/twitter", method=RequestMethod.GET)
	public String home(Principal currentUser, Model model) {
		List<ServiceProviderConnection<TwitterApi>> connections = twitterProvider.getConnections(currentUser.getName());
		if (connections.size() > 0) {
			List<TwitterProfile> connectedProfiles = new ArrayList<TwitterProfile>(connections.size());
			for (ServiceProviderConnection<TwitterApi> connection : connections) {
				connectedProfiles.add(connection.getServiceApi().getUserProfile());
			}			
			model.addAttribute("connectedProfiles", connectedProfiles);
			model.addAttribute(new TweetForm());
			return "twitter/twitter";
		}
		return "redirect:/connect/twitter";
	}

	@RequestMapping(value="/twitter/tweet", method=RequestMethod.POST)
	public String postTweet(Principal currentUser, TweetForm tweetForm) {
		List<ServiceProviderConnection<TwitterApi>> connections = twitterProvider.getConnections(currentUser.getName());
		for (ServiceProviderConnection<TwitterApi> connection : connections) {
			TwitterApi twitter = connection.getServiceApi();
			if (tweetForm.isTweetToAll() || twitter.getProfileId().equals(tweetForm.getScreenName())) {
				twitter.updateStatus(tweetForm.getMessage());
			}
		}
		return "redirect:/twitter";
	}

}