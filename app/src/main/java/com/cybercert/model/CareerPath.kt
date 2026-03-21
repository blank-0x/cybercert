package com.cybercert.model

import androidx.compose.runtime.Immutable

@Immutable
data class CareerPathEntry(
    val name: String,
    val certIds: List<String>,
    val description: String
)

/** Proper display names for cert IDs that appear in career paths. */
val CERT_DISPLAY_NAMES = mapOf(
    "comptia-security-plus"  to "Security+",
    "comptia-cysa-plus"      to "CompTIA CySA+",
    "comptia-pentest-plus"   to "PenTest+",
    "comptia-cloud-plus"     to "Cloud+",
    "eccouncil-ceh"          to "CEH",
    "eccouncil-chfi"         to "CHFI",
    "giac-gcih"              to "GCIH",
    "giac-gcia"              to "GCIA",
    "giac-grem"              to "GREM",
    "giac-gcfe"              to "GCFE",
    "giac-gcfa"              to "GCFA",
    "offensive-security-oscp" to "OSCP",
    "elearnsecurity-ejpt"    to "eJPT",
    "isc2-cissp"             to "CISSP",
    "isc2-ccsp"              to "CCSP",
    "isc2-csslp"             to "CSSLP",
    "isaca-cism"             to "CISM"
)

/** One-line rationale shown in expandable "Why this cert?" row. */
val CERT_WHY_MAP = mapOf(
    "comptia-security-plus"   to "Industry baseline — required or preferred for most security roles",
    "comptia-cysa-plus"       to "Builds on Security+ with defensive/detection focus",
    "comptia-pentest-plus"    to "Mid-level pentesting cert with broad coverage of techniques",
    "comptia-cloud-plus"      to "Validates cloud infrastructure skills across multi-cloud environments",
    "eccouncil-ceh"           to "Well-known ethical hacking cert, widely recognised by employers",
    "eccouncil-chfi"          to "Covers digital forensics methodology and investigation techniques",
    "giac-gcih"               to "GIAC's incident handling cert — deep IR methodology and tools",
    "giac-gcia"               to "Intrusion analysis cert — advanced network traffic and log analysis",
    "giac-grem"               to "Reverse engineering cert — malware analysis with practical lab work",
    "giac-gcfe"               to "Forensic examiner cert — Windows forensics and evidence acquisition",
    "giac-gcfa"               to "Advanced forensic analyst cert — memory and artifact analysis",
    "offensive-security-oscp" to "Hands-on offensive cert — gold standard for pentesters",
    "elearnsecurity-ejpt"     to "Entry-level practical pentesting — good starting point",
    "isc2-cissp"              to "Management-level security cert — widely required for senior roles",
    "isc2-ccsp"               to "Cloud security cert — vendor-neutral, well-respected for cloud roles",
    "isc2-csslp"              to "Covers secure software development lifecycle and practices",
    "isaca-cism"              to "Security management cert — bridges technical and business domains"
)

val CAREER_PATHS = listOf(
    CareerPathEntry(
        name = "SOC Analyst",
        certIds = listOf("comptia-security-plus", "comptia-cysa-plus", "eccouncil-ceh", "giac-gcih"),
        description = "Monitor networks, triage alerts, and respond to security incidents"
    ),
    CareerPathEntry(
        name = "Penetration Tester",
        certIds = listOf("elearnsecurity-ejpt", "comptia-pentest-plus", "offensive-security-oscp", "eccouncil-ceh"),
        description = "Find and exploit vulnerabilities in systems and networks"
    ),
    CareerPathEntry(
        name = "Cloud Security Engineer",
        certIds = listOf("comptia-cloud-plus", "isc2-ccsp", "isc2-cissp"),
        description = "Secure cloud infrastructure and services"
    ),
    CareerPathEntry(
        name = "Incident Responder",
        certIds = listOf("comptia-security-plus", "comptia-cysa-plus", "giac-gcih"),
        description = "Contain, investigate, and recover from security breaches"
    ),
    CareerPathEntry(
        name = "Malware Analyst",
        certIds = listOf("comptia-security-plus", "giac-grem", "eccouncil-ceh"),
        description = "Reverse engineer and analyse malicious software"
    ),
    CareerPathEntry(
        name = "Security Engineer",
        certIds = listOf("comptia-security-plus", "isc2-cissp", "isaca-cism", "isc2-ccsp"),
        description = "Design and implement security systems and controls"
    ),
    CareerPathEntry(
        name = "DevSecOps",
        certIds = listOf("comptia-security-plus", "isc2-ccsp"),
        description = "Integrate security into CI/CD pipelines and development workflows"
    ),
    CareerPathEntry(
        name = "Forensics Analyst",
        certIds = listOf("comptia-security-plus", "giac-grem", "eccouncil-chfi"),
        description = "Collect and analyse digital evidence for investigations"
    )
)
