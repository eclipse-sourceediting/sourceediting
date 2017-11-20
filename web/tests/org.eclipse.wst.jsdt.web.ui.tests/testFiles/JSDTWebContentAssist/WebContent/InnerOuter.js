function outerFunc()
{
			// innerFunc and outerFunc
	function innerFunc()
	{
			// all but localInnerFunc	
		
		function insideInnerFunc()
		{
			var localInnerFunc = function(param1) {
				
			};
			//all the functions
			
		}
			// all but localInnerFunc
		
	}
	
}

	// only outerFunc