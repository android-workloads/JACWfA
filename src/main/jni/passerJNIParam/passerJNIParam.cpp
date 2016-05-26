#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_intel_JACW_jni_Passer4ParamJNI
 * Method:    passer
 * Signature: (JJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_JACW_jni_Passer4ParamJNI_passer
  (JNIEnv * env, jobject c,
		  jlong p0, jlong p1, jlong p2, jlong p3){
	return p0 + p1 + p2 + p3;
}

/*
 * Class:     com_intel_JACW_jni_Passer8ParamJNI
 * Method:    passer
 * Signature: (JJJJJJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_JACW_jni_Passer8ParamJNI_passer
  (JNIEnv * env, jobject c,
		  jlong p0, jlong p1, jlong p2, jlong p3,
		  jlong p4, jlong p5, jlong p6, jlong p7){
	return p0 + p1 + p2 + p3 + p4 + p5 + p6 + p7;
}

/*
 * Class:     com_intel_JACW_jni_Passer12ParamJNI
 * Method:    passer
 * Signature: (JJJJJJJJJJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_JACW_jni_Passer12ParamJNI_passer
  (JNIEnv * env, jobject c,
		  jlong p0, jlong p1, jlong p2, jlong p3,
		  jlong p4, jlong p5, jlong p6, jlong p7,
		  jlong p8, jlong p9, jlong p10, jlong p11){
	return p0 + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11;
}

/*
 * Class:     com_intel_JACW_jni_Passer16ParamJNI
 * Method:    passer
 * Signature: (JJJJJJJJJJJJJJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_JACW_jni_Passer16ParamJNI_passer
  (JNIEnv * env, jobject c,
		  jlong p0, jlong p1, jlong p2, jlong p3,
		  jlong p4, jlong p5, jlong p6, jlong p7,
		  jlong p8, jlong p9, jlong p10, jlong p11,
		  jlong p12, jlong p13, jlong p14, jlong p15){
	return p0 + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15;
}

/*
 * Class:     com_intel_JACW_jni_Passer20ParamJNI
 * Method:    passer
 * Signature: (JJJJJJJJJJJJJJJJJJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_JACW_jni_Passer20ParamJNI_passer
  (JNIEnv * env, jobject c,
		  jlong p0, jlong p1, jlong p2, jlong p3,
		  jlong p4, jlong p5, jlong p6, jlong p7,
		  jlong p8, jlong p9, jlong p10, jlong p11,
		  jlong p12, jlong p13, jlong p14, jlong p15,
		  jlong p16, jlong p17, jlong p18, jlong p19){
	return p0 + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19;
}

/*
 * Class:     com_intel_JACW_jni_Passer24ParamJNI
 * Method:    passer
 * Signature: (JJJJJJJJJJJJJJJJJJJJJJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_JACW_jni_Passer24ParamJNI_passer
  (JNIEnv * env, jobject c,
		  jlong p0, jlong p1, jlong p2, jlong p3,
		  jlong p4, jlong p5, jlong p6, jlong p7,
		  jlong p8, jlong p9, jlong p10, jlong p11,
		  jlong p12, jlong p13, jlong p14, jlong p15,
		  jlong p16, jlong p17, jlong p18, jlong p19,
		  jlong p20, jlong p21, jlong p22, jlong p23){
	return p0 + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 + p23;
}

/*
 * Class:     com_intel_JACW_jni_Passer28ParamJNI
 * Method:    passer
 * Signature: (JJJJJJJJJJJJJJJJJJJJJJJJJJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_JACW_jni_Passer28ParamJNI_passer
  (JNIEnv * env, jobject c,
		  jlong p0, jlong p1, jlong p2, jlong p3,
		  jlong p4, jlong p5, jlong p6, jlong p7,
		  jlong p8, jlong p9, jlong p10, jlong p11,
		  jlong p12, jlong p13, jlong p14, jlong p15,
		  jlong p16, jlong p17, jlong p18, jlong p19,
		  jlong p20, jlong p21, jlong p22, jlong p23,
		  jlong p24, jlong p25, jlong p26, jlong p27){
	return p0 + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 + p23 + p24 + p25 + p26 + p27;
}

/*
 * Class:     com_intel_JACW_jni_Passer32ParamJNI
 * Method:    passer
 * Signature: (JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_JACW_jni_Passer32ParamJNI_passer
  (JNIEnv * env, jobject c,
		  jlong p0, jlong p1, jlong p2, jlong p3,
		  jlong p4, jlong p5, jlong p6, jlong p7,
		  jlong p8, jlong p9, jlong p10, jlong p11,
		  jlong p12, jlong p13, jlong p14, jlong p15,
		  jlong p16, jlong p17, jlong p18, jlong p19,
		  jlong p20, jlong p21, jlong p22, jlong p23,
		  jlong p24, jlong p25, jlong p26, jlong p27,
		  jlong p28, jlong p29, jlong p30, jlong p31){
	return p0 + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 + p23 + p24 + p25 + p26 + p27 + p28 + p29 + p30 + p31;
}

/*
 * Class:     com_intel_JACW_jni_PasserObjectRefJNI
 * Method:    passer
 * Signature: (Lcom/intel/JACW/util/NotImportantObject;JJ)J
 */
JNIEXPORT jlong JNICALL Java_com_intel_JACW_jni_PasserObjectRefJNI_passer
  (JNIEnv * env, jobject c, jobject obj, jlong a, jlong b){
	if (a < 0 || b < 0)
		return -1;
	jclass objClass = env->GetObjectClass(obj);
	jmethodID setFields = env->GetMethodID(objClass, "setFields", "(JJ)V");
	if (setFields == 0)
		return -1;
	jmethodID getSummOfFieldssetFields = env->GetMethodID(objClass, "getSummOfFields", "()J");
	if (getSummOfFieldssetFields == 0)
		return -1;
	env->CallVoidMethod(obj, setFields, a, b);
	jlong result = env->CallLongMethod(obj, getSummOfFieldssetFields);
	return result;
}

#ifdef __cplusplus
}
#endif
