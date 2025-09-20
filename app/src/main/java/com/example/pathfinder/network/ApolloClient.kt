package com.example.pathfinder.network

import com.apollographql.apollo3.ApolloClient

private const val GRAPHQL_URL = "https://backend.revvote.site/graphql"
//private const val GRAPHQL_URL = "http://10.0.2.2:4001/graphql"

val apolloClient = ApolloClient.Builder()
    .serverUrl(GRAPHQL_URL)
    .build()

