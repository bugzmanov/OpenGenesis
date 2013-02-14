---
layout: post
title: First steps - getting started
category: articles
tags: tutorial
year: 2013
month: 1
day: 30
published: true
summary: First steps with OpenGenesis. Demonstrate how to create simple 2 node erlang environment in amazon EC2
image: release.png
---

<script src="/js/jquery.js"></script>
<script src="/js/lightbox.js"></script>

<div class="row">
<div class="span9 columns">
    <h2>Install OpenGenesis (standalone server)</h2>
    <p>Go to <a href="/downloads.html">Downloads section</a> and download latest stable release from "Standalone application" section.
        Unpack downloaded archive</p>

    <h2>Configuring server to work with AWS</h2>
    <div class="alert alert-info">
        You should already have <a href="http://aws.amazon.com/">Amazon AWS</a> account configured
    </div>
    <p>Check that you have your amazon SSH private key (used to access provisioned VMs) somewhere on a disk</p>
    <p>In the directory with unpacked installation, open <code>conf/genesis.properties</code> file.</p>
    <p>Add following properties: </p>
    <pre>genesis.plugin.jclouds.identity = <strong>&lt;YOUR_AWS_IDENTITY&gt;</strong>
genesis.plugin.jclouds.credential = <strong>&lt;YOUR_AWS_PASSWORD&gt;</strong>
genesis.plugin.jclouds.provider = aws-ec2
genesis.plugin.jclouds.endpoint = https://ec2.eu-west-1.amazonaws.com
genesis.system.default.vm.identity = ubuntu   #depends on your image
genesis.system.default.vm.credential = file://<strong>&lt;PATH_TO_AMAZON_SSH_PRIVATE_KEY&gt;</strong></pre>

    <h2>Configuring server to work with hosted chef server</h2>
    <div class="alert alert-info">
        You should either have your own chef server installation or have registered account in <a href="https://community.opscode.com/users/new">Opscode hosted chef</a>
    </div>

    <p>To work with chef hosted server you should have 2 private keys: identity and validator</p>
    <p>The following configuration will allow you to work with <a href="https://community.opscode.com/users/new">Opscode hosted chef</a></p>

    <p>Add following properties to<code>conf/genesis.properties</code>: </p>
    <pre><em># the credentials that will be used to authenticate to the Chef server</em>
genesis.chef.identity = <strong>&lt;YOUR_IDENTITY&gt;</strong>
genesis.chef.credential = file://<strong>&lt;PATH_TO_YOUR_AUTH_PEM&gt;</strong>

<em># validator information to let the nodes to auto-register themselves in the Chef server</em>
genesis.chef.validator.identity = <strong>&lt;YOUR_IDENTITY_FOR_VALIDATOR&gt;</strong>
genesis.chef.validator.credential = file://<strong>&lt;PATH_TO_YOUR_VALIDATOR_PEM&gt;</strong>

genesis.chef.endpoint = https://api.opscode.com/organizations/<strong>&lt;YOUR_ORGANIZATION&gt;</strong>
</pre>

    <h2>You first environment configuration script</h2>
    <p>In create a new file <code>templates/Erlang.genesis</code>. Add following content</p>

    <script src="https://gist.github.com/bugzmanov/60c839424384b01d2374.js"></script>

    <p>This is a simple erlang environment with 2 nodes in it</p>

    <h2>Starting OpenGenesis server</h2>

    <p>For windows users:
        <ul>
            <li>
                <code>bin/genesis.bat install</code>.
            </li>
            <li>
                <code>bin/genesis.bat start</code>.
            </li>
        </ul>
    </p>
    <p>Unix users should run:
        <ul>
            <li><code>bin/genesis.sh install</code></li>
            <li><code>bin/genesis.sh start</code></li>
        </ul></p>
    <p>Then open <code>http://localhost:8080</code> in your browser. You should be able to see login page</p>
    <p>By default genesis has SUPER admin access with following credentials <code>genesis/genesis</code></p>
    <h2>Creating erlang environment</h2>
    <div class="alert">
        <h4>Warning!</h4>
        By default Genesis use in-memory data store.
        This means that after server restart genesis will loose track of created resources and you'll have to delete them manually
    </div>
    <p>In 5 simple steps:
        <ol>
            <li>Create a project</li>
            <li>In the project click "Create new instance"</li>
            <li>In template list select "ErlangInAmazon" and click "Next"</li>
            <li>Fill name and select "Default" environment</li>
            <li>Click create</li>
        </ol>
        After environment instance will become "Ready". You good to start using it.
    </p>
    <h2>Destroying environment</h2>
    <p>When you've done playing with erlang, you can easily destroy this environment. This will force genesis to clean node records from chef server and destroy provisioned virtual machines.</p>

    <h2>Running server demonstration</h2>
    <ul class="thumbnails">
        <li class="span3">
            <a href="/img/posts/getting-started/screenshot.png" class="thumbnail" rel="lightbox">
                <img    src="/img/posts/getting-started/screenshot.png" alt=""/>
            </a>
        </li>
        <li class="span3">
            <a href="/img/posts/getting-started/screenshot2.png" class="thumbnail" rel="lightbox">
                <img src="/img/posts/getting-started/screenshot2.png" alt=""/>
            </a>
        </li>
        <li class="span3">
            <a href="/img/posts/getting-started/screenshot3.png" class="thumbnail" rel="lightbox">
                <img src="/img/posts/getting-started/screenshot3.png" alt=""/>
            </a>
        </li>
    </ul>
</div>
</div>
