package com.jsrdev.screenmatch.repository

import com.jsrdev.screenmatch.model.Series
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SeriesRepository : JpaRepository<Series, UUID>