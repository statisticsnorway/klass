# Migration Testing Module

This module aims to **verify and document that data remains consistent and unchanged after migration**, ensuring integrity across source and target systems.

To ensure both data fidelity and functional correctness, we test the responses from **Klass API endpoints**.

---

## Overview
All test classes inherit from the abstract base class **`AbstractKlassApiDataIntegrityTest`**, either directly or via a package-specific base class.

/migration
├── dataIntegrity
│ ├── changes
│ │ ├── AbstractKlassApiChanges
│ │ ├── KlassApiClassificationChangesCsvTest
│ │ ├── KlassApiClassificationChangesJsonTest
│ │ └── KlassApiClassificationChangesXmlTest
│ ├── classification
│ │ ├── AbstractKlassApiClassificationTest
│ │ ├── KlassApiClassificationByIdJsonTest
│ │ └── KlassApiClassificationByIdXmlTest
│ ├── classifications
│ │ ├── AbstractKlassApiClassifications
│ │ ├── KlassApiClassificationsJsonTest
│ │ └── KlassApiClassificationsXmlTest
│ ├── codes
│ │ ├── AbstractKlassApiCodesTest
│ │ ├── KlassApiClassificationCodesAtCsvTest
│ │ ├── KlassApiClassificationCodesAtJsonTest
│ │ ├── KlassApiClassificationCodesAtXmlTest
│ │ ├── KlassApiClassificationCodesCsvTest
│ │ ├── KlassApiClassificationCodesJsonTest
│ │ └── KlassApiClassificationCodesXmlTest
│ ├── corresponds
│ │ ├── AbstractKlassApiCorrespondsTest
│ │ ├── KlassApiClassificationCorrespondsAtCsvTest
│ │ ├── KlassApiClassificationCorrespondsAtJsonTest
│ │ ├── KlassApiClassificationCorrespondsAtXmlTest
│ │ ├── KlassApiClassificationCorrespondsCsvTest
│ │ ├── KlassApiClassificationCorrespondsJsonTest
│ │ ├── KlassApiClassificationCorrespondsXmlTest
│ │ ├── KlassApiCorrespondenceTablesByIdCsvTest
│ │ ├── KlassApiCorrespondenceTablesByIdJsonTest
│ │ └── KlassApiCorrespondenceTablesByIdXmlTest
│ ├── families
│ │ ├── AbstractKlassApiFamiliesTest
│ │ ├── KlassApiClassificationFamiliesByIdJsonTest
│ │ ├── KlassApiClassificationFamiliesByIdXmlTest
│ │ ├── KlassApiClassificationFamiliesJsonTest
│ │ └── KlassApiClassificationFamiliesXmlTest
│ ├── variant
│ │ ├── AbstractKlassApiVariantTest
│ │ ├── KlassApiClassificationVariantAtTest
│ │ ├── KlassApiClassificationVariantTest
│ │ └── KlassApiVariantByIdTest
│ └── versions
│ │ ├── AbstractKlassApiVersions
│ │ ├── KlassApiVersionByIdCsvTest
│ │ ├── KlassApiVersionByIdJsonTest
│ │ └── KlassApiVersionByIdXmlTest
│ └── AbstractKlassApiDataIntegrityTest
│ └── KlassApiSsbSectionsTest
├── MigrationTestConfig
├── MigrationTestConstants
├── MigrationTestUtils
└── README.md

## What It Tests

- All tests retrieve responses from **two systems**: the original (source) and the migrated (target).
- The following aspects must match:
    - HTTP status codes
    - Response structure
    - All field values (except hostname in links)
    - Response size
- Supports format-specific validation (XML, JSON, CSV)
- **All supported query parameters** are covered at least once across the test suite. 
- **Each failing test returns a detailed fail message** with the mismatched path and differing values, making debugging easier

## Setup

The `MigrationTestConfig` class provides methods to retrieve host values for the **source** and **target** systems used in the tests.

### `getSourceHost()`
Returns the source base URL by checking the following, in order:
1. Property: `source.service.host`
2. Environment variable: `SOURCE_SERVICE_HOST`
3. Default fallback: `http://localhost:8082`

Source Klass Api SSB url: use constant `DATA_SSB_HOST`

### `getTargetHost()`
Returns the target base URL by checking the following, in order:
1. Property: `target.service.host`
2. Environment variable: `TARGET_SERVICE_HOST`
3. Default fallback: `http://localhost:8080`

Target Klass Api Nais Test url: use constant `NAIS_TEST_HOSTNAIS_TEST_HOST`

Set system property: `System.setProperty("<property name>", "<host name>");`

## Details and Running the Tests

### Health Check Before Execution
Before running tests, a **health check** is performed on both source and target by calling `GET {host}/ping`.  
If either host is unavailable the tests are **skipped** (ignored) to prevent false failures.

