(:*******************************************************:)
(: Test: K-SeqExprCast-284                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: Testing timezone field in xs:gYearMonth: the minute component cannot be -60. :)
(:*******************************************************:)
xs:gYearMonth("1999-01-10:60")