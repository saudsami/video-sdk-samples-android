#SDK quickstart

Real-time video immerses people in the sights and sounds of human connections, keeping them engaged in your app longer.

Video Calling enables one-to-one or small-group video chat connections with smooth, jitter-free streaming video. Agora’s Video SDK makes it easy to embed real-time video chat into web, mobile and native apps.

Thanks to Agora’s intelligent and global Software Defined Real-time Network (Agora SD-RTN™), you can rely on the highest available video and audio quality.

This page shows the minimum code you need to integrate high-quality, low-latency Video Calling features into your app using Video SDK.

## Understand the tech

This section explains how you can integrate <Vpd k="PRODUCT" /> features into your <Vpl k="CLIENT" />. The following figure shows the workflow you need to integrate this feature into your <Vpl k="CLIENT" />.

![Video Calling Web UIKit](https://docs.agora.io/en/assets/images/video-call-269cde1fc8aaaa4fcf30a26c31cda13b.png)

To start a session, implement the following steps in your <Vpl k="CLIENT" />:

- *Retrieve a token*: A token is a computer-generated string that authenticates a user when your <Vpl k="CLIENT" /> joins a channel. In this guide you retrieve your token from <Vg k="CONSOLE" />. To see how to create an authentication server for development purposes, see [Implement the authentication workflow](../develop/authentication-workflow). To develop your own token generator and integrate it into your production IAM system, read [Token generators](/video-calling/develop/integrate-token-generation).
- *Join a channel*: Call methods to create and join a channel; <Vpl k="CLIENT" />s that pass the same channel name join the same channel.
- *Send and receive video and audio in the channel*: All users send and receive video and audio streams from all users in the channel.

## Prerequisites

In order to follow this procedure you must have:

- Android Studio 4.1 or higher.
- Android SDK API Level 24 or higher.
- A mobile device that runs Android 4.1 or higher.
- An Agora account and project.
- A computer with Internet access. 
    Ensure that no firewall is blocking your network communication.

## Project setup
